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
open class InitCommandHandler(
    private val slashCommandParser: TelegramSlashCommandParser,
    private val textResourceLoader: TextResourceLoader,
    private val spaceParser: SpaceParser<Update, Chat>,
    private val messageSource: MessageSource,
) : CommandHandler(slashCommandParser, textResourceLoader, spaceParser) {

    override fun getCommand() = COMMAND_INIT

    companion object {
        private const val COMMAND_INIT = "/init"
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update?): Optional<SendMessage> {
        if (input?.message?.from?.bot==true){
            return Optional.empty()
        }

        //不允许私聊中执行指令
        if (input?.message?.from?.id==input?.message?.chat?.id){
            val privateChatCommandMessage = messageSource.getMessage("private.chat.bot.command", LocaleHelper.language(input))
                .orElse(LocaleHelper.EMPTY)
            return SendMessageUtils.compose(spaceParser, input, privateChatCommandMessage)
        }


        val groupInitMessage = messageSource.getMessage("group.init.message", LocaleHelper.language(input),input?.message?.chat?.id,input?.message?.from?.id)
            .orElse(LocaleHelper.EMPTY)
        return SendMessageUtils.compose(spaceParser, input, groupInitMessage)
    }



    override fun canHandle(bot: TelegramBotConfiguration?, input: Update?): Boolean {
        return super.canHandle(bot, input)
    }
}