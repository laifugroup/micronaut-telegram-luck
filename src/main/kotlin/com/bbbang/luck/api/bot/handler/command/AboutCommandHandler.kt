package com.bbbang.luck.api.bot.handler.command

import com.bbbang.luck.api.bot.core.Ordered
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.core.TextResourceLoader
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.core.CommandHandler
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramSlashCommandParser
import jakarta.inject.Singleton
import java.util.*

@Singleton
open class AboutCommandHandler(
    private val slashCommandParser: TelegramSlashCommandParser,
    private val textResourceLoader: TextResourceLoader,
    private val spaceParser: SpaceParser<Update, Chat>
) : CommandHandler(slashCommandParser, textResourceLoader, spaceParser) {

    override fun getCommand() = COMMAND_ABOUT

    companion object {
        private const val COMMAND_ABOUT = "/about"
    }

    override fun getOrder() = Ordered.ABOUT



    override fun handle(bot: TelegramBotConfiguration?, input: Update?): Optional<SendMessage> {
        return super.handle(bot, input)
    }

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update?): Boolean {
        println("---about")
        return super.canHandle(bot, input)
    }


}