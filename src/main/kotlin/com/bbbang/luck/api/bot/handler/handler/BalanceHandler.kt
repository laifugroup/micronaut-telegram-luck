package com.bbbang.luck.api.bot.handler.handler

import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.handler.callback.BalanceCallbackHandler
import com.bbbang.luck.api.bot.telegram.AnswerCallbackQuery
import com.bbbang.luck.service.LuckWalletService
import com.bbbang.luck.service.wrapper.LuckUserServiceWrapper
import com.bbbang.luck.utils.LocaleHelper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.ReplyKeyboardMarkup
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.ParseMode
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.core.SendMessageUtils
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Singleton
open class BalanceHandler(private val spaceParser: SpaceParser<Update, Chat>
                          , private val messageSource: MessageSource
                          , private val luckWalletService: LuckWalletService
    ) : TelegramHandler<SendMessage> {

    companion object{
        const val BALANCE = "(?i)(ye|余额|查|yue)"
    }

    override fun getOrder() = Ordered.BALANCE

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("---balance")
        val match=  input.message?.text?.matches(BALANCE.toRegex())
        return  match!=null && match
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<SendMessage>{
        //不允许机器人
        if (input?.message?.from?.bot == true) {
            return Optional.empty()
        }
        //不允许私聊中执行指令
        if (input?.message?.from?.id == input?.message?.chat?.id) {
            val privateChatCommandMessage =
                messageSource.getMessage("private.chat.bot.command", LocaleHelper.language(input))
                    .orElse(LocaleHelper.EMPTY)
            return SendMessageUtils.compose(spaceParser, input, privateChatCommandMessage)
        }

        val chat= spaceParser.parse(input)

       val wallet= luckWalletService.findWalletByUserId(input.message?.from?.id,input.message?.chat?.id)
        val credit= wallet.credit
        val userId=wallet.userId
        val creditValue = credit?.toDouble() ?: 0.0
        val formattedCredit = String.format("%.2f", creditValue)
        val luckBalance = messageSource.getMessage("luck.balance", LocaleHelper.language(input), userId, formattedCredit)
            .orElse(LocaleHelper.EMPTY)

       // val sendMessage=   SendMessageUtils.compose(spaceParser,input,luckBalance,ParseMode.MARKDOWN)
        val sendMessage =  SendMessage().apply {
            chatId = chat.getOrNull()?.id.toString()
            replyToMessageId=input.message?.messageId?.toString()
            text=luckBalance
            parseMode=ParseMode.MARKDOWN.toString()
        }
        return  Optional.of(sendMessage)
    }


}
