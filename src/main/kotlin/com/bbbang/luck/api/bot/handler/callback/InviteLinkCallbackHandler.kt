package com.bbbang.luck.api.bot.handler.callback

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.telegram.AnswerCallbackQuery
import com.bbbang.luck.api.bot.telegram.CreateChatInviteLink
import com.bbbang.luck.api.bot.telegram.TelegramBotAPI
import com.bbbang.luck.api.bot.type.InviteType
import com.bbbang.luck.configuration.properties.BotWebHookProperties
import com.bbbang.luck.domain.bo.LuckInviteBO
import com.bbbang.luck.service.LuckInviteService
import com.bbbang.luck.service.LuckWalletService
import com.bbbang.luck.service.wrapper.LuckUserServiceWrapper
import com.bbbang.luck.utils.LocaleHelper
import com.bbbang.luck.utils.UserNameHelper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.core.SendMessageUtils
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.xml.crypto.dsig.DigestMethod

@Singleton
open class InviteLinkCallbackHandler(private val spaceParser: SpaceParser<Update, Chat>
                                     , private val messageSource: MessageSource
                                     , private val luckInviteService: LuckInviteService
                                      ,private val telegramBotAPI: TelegramBotAPI
                                     ,private val botWebHookProperties: BotWebHookProperties


    ) : TelegramHandler<SendMessage> {

    companion object{
        const val INVITE_LINK = CallbackData.INVITE_LINK
    }

    override fun getOrder() = Ordered.INVITE_LINK

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("---invite_link callback")
        val match=  input.callbackQuery?.data?.matches(INVITE_LINK.toRegex())
        val m= match!=null && match
        return m
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<SendMessage>{
        //不允许私聊中执行指令
        if (input.callbackQuery?.from?.id == input.callbackQuery?.message?.chat?.id) {
            val privateChatCommandMessage =
                messageSource.getMessage("private.chat.bot.command", LocaleHelper.language(input))
                    .orElse(LocaleHelper.EMPTY)
            return SendMessageUtils.compose(spaceParser, input, privateChatCommandMessage)
        }

        val inviteLink=luckInviteService.findByUserId(input.callbackQuery?.from?.id!!)
        if (inviteLink!=null && inviteLink.expiredAt?.isAfter(LocalDateTime.now()) == true){
            val luckInviteLink =messageSource.getMessage("luck.invite.link", LocaleHelper.language(input),UserNameHelper.getUserName(input), inviteLink.url).orElse(LocaleHelper.EMPTY)
            val sendInviteMessage = SendMessage().apply {
                this.chatId=input.callbackQuery.message.chat?.id.toString()
                this.text = luckInviteLink
            }
            return Optional.of(sendInviteMessage)
        }


        val createChatInviteLink = CreateChatInviteLink(
            chatId = input.callbackQuery?.message?.chat?.id,
            name = input.callbackQuery?.message?.chat?.title,
            expireDate = LocalDate.now().plusDays(365).atStartOfDay(ZoneOffset.UTC).toEpochSecond().toInt(),
            memberLimit = 1000,
            createsJoinRequest = false
        )

           val chatInviteLink= telegramBotAPI.createChatInviteLink(
               botWebHookProperties.httpApiToken,
               createChatInviteLink.chatId,
               createChatInviteLink.name,
               createChatInviteLink.expireDate,
               createChatInviteLink.memberLimit,
               createChatInviteLink.createsJoinRequest
           )

           //println("chatInviteLink=${chatInviteLink.result.inviteLink}")
        val instant = Instant.ofEpochSecond(createChatInviteLink.expireDate!!.toLong())
        val result=chatInviteLink.result
        val urlHash = MessageDigest.getInstance("MD5").digest(result.inviteLink?.toByteArray(StandardCharsets.UTF_8))?.let {
            val sb = StringBuilder()
            it.forEach { b -> sb.append(String.format("%02x", b)) }
            sb.toString()
        }

       val luckInviteBO= LuckInviteBO().apply {
            this.userId = input.callbackQuery.from.id
            this.url = result.inviteLink
            this.urlHash =urlHash
            this.groupId =createChatInviteLink.chatId
            this.status = InviteType.ENABLE.code
            this.expiredAt =instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
        }

        val luckInvite=luckInviteService.save(luckInviteBO)
        val luckInviteLink =messageSource.getMessage("luck.invite.link", LocaleHelper.language(input),UserNameHelper.getUserName(input), luckInvite.url).orElse(LocaleHelper.EMPTY)

        val sendInviteMessage = SendMessage().apply {
            this.chatId=input.callbackQuery.message.chat?.id.toString()
            this.text = luckInviteLink
        }
        return Optional.of(sendInviteMessage)
    }


}
