package com.bbbang.luck.api.bot.handler.callback

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.telegram.AnswerCallbackQuery
import com.bbbang.luck.api.bot.telegram.CreateChatInviteLink
import com.bbbang.luck.api.bot.telegram.TelegramBotAPI
import com.bbbang.luck.api.bot.type.InviteType
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

@Singleton
open class InviteQueryCallbackHandler(private val spaceParser: SpaceParser<Update, Chat>
                                      , private val messageSource: MessageSource
                                      , private val  inviteLogService: LuckInviteLogService
                                      , private val telegramBotAPI: TelegramBotAPI
                                      , private val botWebHookProperties: BotWebHookProperties

    ) : TelegramHandler<AnswerCallbackQuery> {

    companion object{
        const val INVITE_QUERY = CallbackData.INVITE_QUERY
    }

    override fun getOrder() = Ordered.INVITE_QUERY

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("---invite_query callback")
        val match=  input.callbackQuery?.data?.matches(INVITE_QUERY.toRegex())
        val m= match!=null && match
        return m
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<AnswerCallbackQuery>{
      val userInviteStatistics=inviteLogService.findUserInviteStatistics(input.callbackQuery.from.id?:0)
        val inviteQuery = messageSource.getMessage("luck.invite.query", LocaleHelper.language(input),
            userInviteStatistics.currentDay, userInviteStatistics.currentMonth, userInviteStatistics.total,).orElse(LocaleHelper.EMPTY)
        val bindList=StringBuilder()
        userInviteStatistics.inviteLogList?.forEach {
            bindList.append("${it.createdAt?.format(DateTimeFormatter.ofPattern(SimpleDateFormatHelper.yyMMddHHmm))}  ${if((it.remark?.length ?: 0) < 5) it.remark else it.remark?.substring(0,5)}\n")
        }

        val answerCallbackQuery = AnswerCallbackQuery(input.callbackQuery.id,  "$inviteQuery \n${bindList}", true, null, null)
        return Optional.of(answerCallbackQuery)
    }


}
