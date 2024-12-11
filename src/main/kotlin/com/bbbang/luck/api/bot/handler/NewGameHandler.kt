package com.bbbang.luck.api.bot.handler

import com.bbbang.luck.api.bot.handler.HelloWorldHandler.Companion.HELLO
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
import jakarta.inject.Singleton
import java.util.*

@Singleton
class NewGameHandler(private val spaceParser: SpaceParser<Update, Chat>,
                     private val objectMapper: ObjectMapper) : TelegramHandler<SendPhoto> {

    companion object{
        const val NEW_GAME_COMMAND = "newGame"
    }

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
                    callbackData="yue"
                    //url=""
                }
            ),
            listOf(
                InlineKeyboardButton().apply {
                    text="提现"
                    callbackData="query"
                    //url=""
                },
                InlineKeyboardButton().apply {
                    text="充值"
                    callbackData="balance"
                    url="https://baidu.com"
                },
                InlineKeyboardButton().apply {
                    text="玩法"
                    callbackData="balance"
                },
                InlineKeyboardButton().apply {
                    text="余额"
                    callbackData="balance"
                }
            ),
            listOf(
                InlineKeyboardButton().apply {
                    text="推广链接"
                    callbackData="query"
                    //url=""
                },
                InlineKeyboardButton().apply {
                    text="推广查询"
                    callbackData="balance"
                },
                InlineKeyboardButton().apply {
                    text="返水统计"
                    callbackData="balance"
                },
                InlineKeyboardButton().apply {
                    text="游戏报表"
                    callbackData="balance"
                }
            ),
            listOf(
                InlineKeyboardButton().apply {
                    text="财务"
                    callbackData="query"
                    //url=""
                },
                InlineKeyboardButton().apply {
                    text="客服"
                    callbackData="balance"
                },

            )
        )
        val inlineKeyboard= objectMapper.writeValueAsString(keyboard)
        return SendPhotoUtils.compose(spaceParser,input,photo,caption,inlineKeyboard, ParseMode.MARKDOWN)
    }

}