package com.bbbang.luck.api.bot.handler.command

import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.helper.SassIdHelper
import com.bbbang.luck.service.wrapper.LuckUserServiceWrapper
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
    private val userServiceWrapper: LuckUserServiceWrapper
) : CommandHandler(slashCommandParser, textResourceLoader, spaceParser) {

    override fun getCommand() = COMMAND_START

    companion object {
        private const val COMMAND_START = "/start"
    }

    override fun getOrder() = Ordered.START


    override fun handle(bot: TelegramBotConfiguration?, input: Update?): Optional<SendMessage> {
        if (input?.message?.from?.bot==true){
            return Optional.empty()
        }
        val from=input?.message?.from
        val sassId=SassIdHelper.getSassId(input?.message?.chat?.id,input?.message?.from)
        val user = userServiceWrapper.findByBotUser(from,sassId,input?.message?.chat?.id,input?.message?.chat?.type)
        val welcomeStart = messageSource.getMessage("welcome.start", LocaleHelper.language(input), "1级","优秀玩家",user?.groupId,user?.botUserId)
            .orElse(LocaleHelper.EMPTY)
        return SendMessageUtils.compose(spaceParser, input, welcomeStart)
    }



    override fun canHandle(bot: TelegramBotConfiguration?, input: Update?): Boolean {
        println("---start")
        return super.canHandle(bot, input)
    }
}