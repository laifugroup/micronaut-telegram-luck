package com.bbbang.luck.api.bot.handler.command

import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.service.wrapper.LuckPlatformServiceWrapper
import com.bbbang.luck.service.wrapper.LuckUserServiceWrapper
import com.bbbang.luck.utils.ChatHelper
import com.bbbang.luck.utils.LocaleHelper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.core.SendMessageUtils
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import java.util.Optional

/**
 * 检测在群组中机器人🤖是否初始化
 */
@Singleton
open class BotInitTipsCommandHandler(private val spaceParser: SpaceParser<Update, Chat>,
                                     private val messageSource:MessageSource,
                                     private val platformServiceWrapper: LuckPlatformServiceWrapper) : TelegramHandler<SendMessage> {

    override fun getOrder() = Ordered.INIT_PRE_VERIFY

    //检查是否初始化bot
    override fun canHandle(bot: TelegramBotConfiguration?, input: Update):Boolean {
        println("---initTips")
        val platform=platformServiceWrapper.findByGroupId(ChatHelper.getChatId(input))
        val eq= platform == null
        return eq
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<SendMessage> {
        if (input.message?.from?.bot==true){
            return Optional.empty()
        }
        val groupInitTips = messageSource.getMessage("group.init.tips", LocaleHelper.language(input),input.message?.chat?.id,input.message?.from?.id)
            .orElse(LocaleHelper.EMPTY)
        return SendMessageUtils.compose(spaceParser, input, groupInitTips)
    }




}
