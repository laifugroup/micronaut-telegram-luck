package com.bbbang.luck.api.bot.handler.handler

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.utils.LocaleHelper
import com.bbbang.luck.utils.SendPhotoUtils
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.InlineKeyboardButton
import io.micronaut.chatbots.telegram.api.InlineKeyboardMarkup
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.ParseMode
import io.micronaut.chatbots.telegram.api.send.SendPhoto
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import java.util.*

@Singleton
class NewGameHandler(private val spaceParser: SpaceParser<Update, Chat>,
                     private val objectMapper: ObjectMapper,
                     private val messageSource: MessageSource) : TelegramHandler<SendPhoto> {

    companion object{
        const val NEW_GAME_COMMAND = "newGame"
    }

    override fun getOrder() = Ordered.NEW_GAME

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("------------------:NewGameHandler")
        val match=  input.message?.text?.matches(NEW_GAME_COMMAND.toRegex())
        return match!=null && match
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<SendPhoto>{
        val photo="https://raw.githubusercontent.com/laifugroup/luck-vertx/refs/heads/master/src/main/resources/views/img/luck_boom.jpg"
        val caption="[luck]发了100U的红包，快来抢🧧"
        val keyboard= InlineKeyboardMarkup()
        keyboard.inlineKeyboard= listOf(
            listOf(
                InlineKeyboardButton().apply {
                    text="🧧抢红包[1/6]总$100U雷3"
                    callbackData=CallbackData.GRAB_RED_PACKET
                    //url=""
                }
            ),
            listOf(
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.withdrawal", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.WITHDRAWAL
                    //url=""
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.recharge", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.RECHARGE
                    url="https://baidu.com"
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.play_rule", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.PLAY_RULE
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.balance", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData= CallbackData.BALANCE
                }
            ),
            listOf(
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.invite_link", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.INVITE_LINK
                    //url=""
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.invite_query", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.INVITE_QUERY
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.water_rate", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.WATER_RATE
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.game_report", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.GAME_REPORT
                }
            ),
            listOf(
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.cashier", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.CASHIER
                    //url=""
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.customer_service", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.CUSTOMER_SERVICE
                },
            )
        )
        val inlineKeyboard= objectMapper.writeValueAsString(keyboard)
        return SendPhotoUtils.compose(spaceParser,input,photo,caption,inlineKeyboard, ParseMode.MARKDOWN)
    }

}