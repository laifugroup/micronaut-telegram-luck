package com.bbbang.luck.api.bot.handler.callback

import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.telegram.AnswerCallbackQuery
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import jakarta.inject.Singleton
import java.util.Optional

@Singleton
open class BalanceCallbackHandler(private val spaceParser: SpaceParser<Update, Chat>) : TelegramHandler<AnswerCallbackQuery> {
    companion object{
        const val BALANCE = "(?i)(ye|余额|查|yue)"
    }
    override fun getOrder() = Ordered.BALANCE

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
       println("------------------:BalanceCallbackHandler")
        val match=  input.callbackQuery?.data?.matches(BALANCE.toRegex())
        val m= match!=null && match
        return m
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<AnswerCallbackQuery>{
        val answerCallbackQuery = AnswerCallbackQuery(input.callbackQuery.id, "zhangsan：余额：200", true, null, null)
        return Optional.of(answerCallbackQuery)
    }


}
