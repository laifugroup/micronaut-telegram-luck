package com.bbbang.luck.api.bot.handler.handler

import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.core.SendMessageUtils
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import jakarta.inject.Singleton
import java.util.*

@Singleton
class HelloWorldHandler(private val spaceParser: SpaceParser<Update, Chat>) : TelegramHandler<SendMessage> {

    companion object{
        const val HELLO = "hello"
    }

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("------------------:HelloWorldHandler")
        val match=  input.message?.text?.matches(HELLO.toRegex())
        return match!=null && match
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<SendMessage> =
        SendMessageUtils.compose(spaceParser, input, "Hello World")
}