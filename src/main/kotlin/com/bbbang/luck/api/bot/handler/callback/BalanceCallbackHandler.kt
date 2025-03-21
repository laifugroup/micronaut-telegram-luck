package com.bbbang.luck.api.bot.handler.callback

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.telegram.AnswerCallbackQuery
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
import java.util.*

@Singleton
open class BalanceCallbackHandler(private val spaceParser: SpaceParser<Update, Chat>
        ,private val messageSource: MessageSource
        ,private val luckWalletService: LuckWalletService
    ) : TelegramHandler<AnswerCallbackQuery> {

    companion object{
        const val BALANCE = CallbackData.BALANCE
    }

    override fun getOrder() = Ordered.BALANCE

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("---balance callback")
        val match=  input.callbackQuery?.data?.matches(BALANCE.toRegex())
        val m= match!=null && match
        return m
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<AnswerCallbackQuery>{

       val wallet= luckWalletService.findWalletByUserId(input.callbackQuery?.from?.id,input.callbackQuery?.message?.chat?.id)
        val credit= wallet.credit
        val userId=wallet.userId
        val creditValue = credit?.toDouble()
        val formattedCredit = String.format("%.2f", creditValue)
        val luckBalance = messageSource.getMessage("luck.balance", LocaleHelper.language(input),userId,formattedCredit)
            .orElse(LocaleHelper.EMPTY)
        val answerCallbackQuery = AnswerCallbackQuery(input.callbackQuery.id, luckBalance, true, null, null)
        return Optional.of(answerCallbackQuery)
    }


}
