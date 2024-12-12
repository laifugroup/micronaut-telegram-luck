package com.bbbang.luck.api.bot.handler.callback

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.telegram.AnswerCallbackQuery
import com.bbbang.luck.api.bot.telegram.CreateChatInviteLink
import com.bbbang.luck.api.bot.telegram.TelegramBotAPI
import com.bbbang.luck.configuration.properties.BotWebHookProperties
import com.bbbang.luck.service.LuckInviteService
import com.bbbang.luck.service.LuckWalletService
import com.bbbang.luck.service.wrapper.LuckUserServiceWrapper
import com.bbbang.luck.utils.LocaleHelper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.core.SendMessageUtils
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

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
        if (input.message?.from?.id == input.message?.chat?.id) {
            val privateChatCommandMessage =
                messageSource.getMessage("private.chat.bot.command", LocaleHelper.language(input))
                    .orElse(LocaleHelper.EMPTY)
            return SendMessageUtils.compose(spaceParser, input, privateChatCommandMessage)
        }



        val createChatInviteLink = CreateChatInviteLink(chatId = input.message?.chat?.id,
            name = input.message?.chat?.title,
            expireDate = LocalDate.now().plusDays(365).atStartOfDay(ZoneOffset.UTC).toEpochSecond().toInt(),
            memberLimit = 1000,
            createsJoinRequest = false
        )

       val chatInviteLink= telegramBotAPI.createChatInviteLink(botWebHookProperties.httpApiToken,createChatInviteLink)

        chatInviteLink.result.inviteLink
//        LuckInviteBO().apply {
//            this.userId = user.id
//            this.url = inviteLink?.inviteLink
//            this.urlHash = DigestUtils.md5Hex(this.url?.toByteArray(Charsets.UTF_8))
//            this.groupId = chat?.id
//            this.status = InviteType.ENABLE.code
//            this.expiredAt = LocalDateTime.ofEpochSecond(createChatInviteLink.expireDate.toLong(), 0, ZoneOffset.UTC)
//        }

        return Optional.empty()


//       val wallet= luckWalletService.findWalletByUserId(input.callbackQuery?.from?.id,input.callbackQuery?.message?.chat?.id)
//        val credit= wallet.credit
//        val userId=wallet.userId
//        val luckBalance = messageSource.getMessage("luck.balance", LocaleHelper.language(input),userId,credit)
//            .orElse(LocaleHelper.EMPTY)
//        val answerCallbackQuery = AnswerCallbackQuery(input.callbackQuery.id, luckBalance, true, null, null)
//        return Optional.of(answerCallbackQuery)
    }


}
