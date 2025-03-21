package com.bbbang.luck.api.bot.handler.callback

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.telegram.AnswerCallbackQuery
import com.bbbang.luck.api.bot.telegram.CreateChatInviteLink
import com.bbbang.luck.api.bot.telegram.TelegramBotAPI
import com.bbbang.luck.api.bot.type.InviteType
import com.bbbang.luck.configuration.TronConfiguration
import com.bbbang.luck.configuration.properties.BotWebHookProperties
import com.bbbang.luck.domain.bo.LuckInviteBO
import com.bbbang.luck.helper.SimpleDateFormatHelper
import com.bbbang.luck.service.LuckInviteLogService
import com.bbbang.luck.service.LuckInviteService
import com.bbbang.luck.service.LuckWalletService
import com.bbbang.luck.service.wrapper.LuckUserServiceWrapper
import com.bbbang.luck.utils.LocaleHelper
import com.bbbang.luck.utils.UserNameHelper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.ParseMode
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
import java.time.format.DateTimeFormatter
import java.util.*
import javax.xml.crypto.dsig.DigestMethod
import kotlin.jvm.optionals.getOrNull

@Singleton
open class RechargeCallbackHandler(private val spaceParser: SpaceParser<Update, Chat>
                                   , private val messageSource: MessageSource
                                   , private val  inviteLogService: LuckInviteLogService
                                   ,private  val tronConfiguration: TronConfiguration
    ) : TelegramHandler<SendMessage> {

    companion object{
        const val RECHARGE = CallbackData.RECHARGE
    }

    override fun getOrder() = Ordered.RECHARGE

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("---recharge callback")
        val match=  input.callbackQuery?.data?.matches(RECHARGE.toRegex())
        val m= match!=null && match
        return m
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<SendMessage>{
        val chat= spaceParser.parse(input)
        val creditUpTips = messageSource.getMessage("credit.up.tips", LocaleHelper.language(input),input.callbackQuery?.from?.username,tronConfiguration.rechargeAddress).orElse(LocaleHelper.EMPTY)
        val sendMessage =  SendMessage().apply {
            chatId = chat.getOrNull()?.id.toString()
            text=creditUpTips
            parseMode=ParseMode.MARKDOWN.toString()
        }
        return  Optional.of(sendMessage)
    }


}
