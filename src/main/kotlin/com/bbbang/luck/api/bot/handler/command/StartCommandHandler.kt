package com.bbbang.luck.api.bot.handler.command

import com.bbbang.luck.utils.LocaleHelper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.core.TextResourceLoader
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.core.CommandHandler
import io.micronaut.chatbots.telegram.core.SendMessageUtils
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramSlashCommandParser
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import java.util.*

@Singleton
open class StartCommandHandler(
    private val slashCommandParser: TelegramSlashCommandParser,
    private val textResourceLoader: TextResourceLoader,
    private val spaceParser: SpaceParser<Update, Chat>,
    private val messageSource: MessageSource,
) : CommandHandler(slashCommandParser, textResourceLoader, spaceParser) {

    override fun getCommand() = COMMAND_START

    companion object {
        private const val COMMAND_START = "/start"
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update?): Optional<SendMessage> {
        if (input?.message?.from?.bot==true){
            return Optional.empty()
        }
        val welcomeStart = messageSource.getMessage("welcome.start", LocaleHelper.language(input), "lv0","优秀玩家",input?.message?.chat?.id,input?.message?.from?.id)
            .orElse(LocaleHelper.EMPTY)
        return SendMessageUtils.compose(spaceParser, input, welcomeStart)
    }



    override fun canHandle(bot: TelegramBotConfiguration?, input: Update?): Boolean {
        println("------------------:BalanceHandler")
        return super.canHandle(bot, input)
    }
}