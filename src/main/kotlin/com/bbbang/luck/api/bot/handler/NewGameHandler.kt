package com.bbbang.luck.api.bot.handler

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

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean = input.message.text.matches(
        NEW_GAME_COMMAND.toRegex())

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<SendPhoto>{
        val photo="https://raw.githubusercontent.com/laifugroup/luck-vertx/refs/heads/master/src/main/resources/views/img/luck_boom.jpg"
        val caption="砍一刀🧧"
        val keyboard= InlineKeyboardMarkup()
        keyboard.inlineKeyboard= listOf(
            listOf(
                InlineKeyboardButton().apply {
                    text="砍一刀[1/6]"
                    callbackData="battle"
                    //url=""
                }
            ),
            listOf(
                InlineKeyboardButton().apply {
                    text="查询"
                    callbackData="query"
                    //url=""
                },
                InlineKeyboardButton().apply {
                    text="余额"
                    callbackData="balance"
                    url="https://baidu.com"
                }
            )
        )
        val inlineKeyboard= objectMapper.writeValueAsString(keyboard)
        return SendPhotoUtils.compose(spaceParser,input,photo,caption,inlineKeyboard, ParseMode.MARKDOWN)
    }

}