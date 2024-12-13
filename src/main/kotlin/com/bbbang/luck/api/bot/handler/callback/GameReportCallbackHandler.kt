package com.bbbang.luck.api.bot.handler.callback

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.telegram.AnswerCallbackQuery
import com.bbbang.luck.helper.SimpleDateFormatHelper
import com.bbbang.luck.service.LuckCreditLogService
import com.bbbang.luck.service.LuckWalletService
import com.bbbang.luck.service.wrapper.LuckUserServiceWrapper
import com.bbbang.luck.utils.LocaleHelper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Singleton
open class GameReportCallbackHandler(private val spaceParser: SpaceParser<Update, Chat>
                                     , private val messageSource: MessageSource
                                     , private val luckCreditLogService: LuckCreditLogService,
    ) : TelegramHandler<AnswerCallbackQuery> {

    companion object{
        const val GAME_REPORT = CallbackData.GAME_REPORT
    }

    override fun getOrder() = Ordered.GAME_REPORT

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("---game_report callback")
        val match=  input.callbackQuery?.data?.matches(GAME_REPORT.toRegex())
        val m= match!=null && match
        return m
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<AnswerCallbackQuery>{
        val it=luckCreditLogService.findGameStatistics(input.callbackQuery?.from?.id?:0)

        val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern(SimpleDateFormatHelper.yyyyMMdd))
        val profit=  (it.compensationSum?: BigDecimal.ZERO)?.add(it.grabSum?:BigDecimal.ZERO)
            ?.minus(it.sendSum?: BigDecimal.ZERO)?.minus(it.boomSum?:BigDecimal.ZERO)

        val luckGameReport = messageSource.getMessage( "luck.game.report",LocaleHelper.language(input),
            today,profit,(it.sendSum?:BigDecimal.ZERO)?.negate(),it.compensationSum?:BigDecimal.ZERO,it.grabSum?:BigDecimal.ZERO,(it.boomSum?: BigDecimal.ZERO)?.negate())
            .orElse(LocaleHelper.EMPTY)

        val answerCallbackQuery = AnswerCallbackQuery(input.callbackQuery.id, luckGameReport, true, null, null)
        return Optional.of(answerCallbackQuery)
    }


}
