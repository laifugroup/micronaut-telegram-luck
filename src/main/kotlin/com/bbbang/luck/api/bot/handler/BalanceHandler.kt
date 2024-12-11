//package com.bbbang.luck.api.bot.handler
//
//import com.bbbang.luck.api.bot.handler.HelloWorldHandler.Companion.HELLO
//import com.bbbang.luck.service.LuckWalletService
//import com.bbbang.luck.service.wrapper.LuckUserServiceWrapper
//import io.micronaut.chatbots.core.SpaceParser
//import io.micronaut.chatbots.telegram.api.Chat
//import io.micronaut.chatbots.telegram.api.Update
//import io.micronaut.chatbots.telegram.api.send.SendMessage
//import io.micronaut.chatbots.telegram.core.SendMessageUtils
//import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
//import io.micronaut.chatbots.telegram.core.TelegramHandler
//import jakarta.inject.Inject
//import jakarta.inject.Singleton
//import java.util.Optional
//
///**
// * 余额查询
// */
//@Singleton
//class BalanceHandler(private val spaceParser: SpaceParser<Update, Chat>) : TelegramHandler<SendMessage> {
//    @Inject
//    lateinit var luckUserService: LuckUserServiceWrapper
//    @Inject
//    lateinit var luckWalletService: LuckWalletService
//
//
//    companion object{
//        const val BALANCE = "(?i)(ye|余额|查|yue)"
//    }
//
//
//
//    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
//        val match=  input.message?.text?.matches(BALANCE.toRegex())
//        return match!=null && match
//    }
//
//    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<SendMessage> {
//
//       return SendMessageUtils.compose(spaceParser, input, "余额：222")
//    }
//
//}