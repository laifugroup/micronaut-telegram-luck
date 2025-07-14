package com.bbbang.luck.api.bot.handler.callback

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.telegram.AnswerCallbackQuery
import com.bbbang.luck.api.bot.telegram.EditMessageCaption
import com.bbbang.luck.api.bot.type.CreditLogType
import com.bbbang.luck.configuration.properties.LuckProperties
import com.bbbang.luck.configuration.properties.ServiceProperties
import com.bbbang.luck.configuration.properties.TronProperties
import com.bbbang.luck.domain.bo.LuckGoodLuckBO
import com.bbbang.luck.domain.po.LuckSendLuckPO
import com.bbbang.luck.domain.vo.LuckSendLuckVO
import com.bbbang.luck.helper.InlineKeyboardMarkupHelper
import com.bbbang.luck.service.LuckGoodLuckService
import com.bbbang.luck.service.LuckSendLuckService
import com.bbbang.luck.service.LuckWalletService
import com.bbbang.luck.service.wrapper.LuckUserServiceWrapper
import com.bbbang.luck.utils.LocaleHelper
import com.bbbang.luck.utils.UserNameHelper
import com.bbbang.parent.utils.BigDecimalUtils
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.InlineKeyboardButton
import io.micronaut.chatbots.telegram.api.InlineKeyboardMarkup
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.ParseMode
import io.micronaut.chatbots.telegram.api.send.Send
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.api.send.SendPhoto
import io.micronaut.chatbots.telegram.core.SendMessageUtils
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import reactor.core.publisher.Mono.delay
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

@Singleton
open class GrabCallbackHandler(private val spaceParser: SpaceParser<Update, Chat>
                               , private val messageSource: MessageSource
                               , private val luckWalletService: LuckWalletService
                               , private val luckProperties: LuckProperties
                               , private val tronProperties: TronProperties
                               , private val luckGoodLuckService: LuckGoodLuckService
                               , private val luckSendLuckService: LuckSendLuckService
                               , private val serviceProperties: ServiceProperties
                               , private val objectMapper: ObjectMapper,
) : TelegramHandler<Send> {


    private val locks = ConcurrentHashMap<String, Lock>()

    companion object{
        const val GRAB_RED_PACKET = CallbackData.GRAB_RED_PACKET
    }

    override fun getOrder() = Ordered.GRAB_RED_PACKET

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("---grab_red_pack callback")
        val match=  input.callbackQuery?.data?.startsWith(GRAB_RED_PACKET)
        val m= match!=null && match
        return m
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<Send>{
        val redPackDataStr = input.callbackQuery.data.split("|")
        val redPackId=redPackDataStr[1]
        val redPackDataLock = locks.computeIfAbsent(redPackId) { ReentrantLock() }
        redPackDataLock.lock()
        try {
            return handleMe(bot, input)
        } finally {
            redPackDataLock.unlock()
        }
    }
     @Transactional
     open fun handleMe(bot: TelegramBotConfiguration?, input: Update): Optional<Send>{

        val botUser = input.callbackQuery.from
        val locale = LocaleHelper.language(input)
        val chatId=input.callbackQuery.message.chat.id
        val messageId=input.callbackQuery.message.messageId

        val redPackMessage = input.callbackQuery.message.replyToMessage.text
        val pattern = "[-|/]".toRegex()
        val split = redPackMessage.split(pattern)
        val total = split[0]
        val boomNumber = split[1]
        //val dollar=Integer.valueOf(total)*100
        val redPackDataStr = input.callbackQuery.data.split("|")
        val redPackId=redPackDataStr[1].toLong()
        val sendLuckUserId=redPackDataStr[2].toLong()
        val oddsCredit = luckProperties.odds.multiply(total.toBigDecimal())


        //1.查询用户，如果用户不存在就新建用户
        //2.验证余额是否足够倍数
        //3.查询是否有抢红包记录，如果无抢空包记录，扣除额度并增加记录
        //4.查询抢红包合计数量，并推送显示
        //5.计算红包结果，并更新抢红包结果。返回扣除额度，并根据结果更新余额分值
        val counts=luckGoodLuckService.countByLuckRedPackId(redPackId)
        if (counts>=luckProperties.redPackNumbers){
            //红包已经抢完
            return Optional.empty()
        }
        val wallet=luckWalletService.findWalletByUserId(input.callbackQuery?.from?.id,input.callbackQuery?.message?.chat?.id)
        if (wallet.credit?.compareTo(oddsCredit)== BigDecimalUtils.SMALL) {
            val luckInsufficientBalance = messageSource.getMessage("luck.insufficient.balance",LocaleHelper.language(input), UserNameHelper.getUserName(input),botUser.id,wallet.credit,tronProperties.rechargeAddress)
                .orElse(LocaleHelper.EMPTY)
            val send=SendMessage().apply {
                this.text=luckInsufficientBalance
                this.chatId=chatId
                this.parseMode=ParseMode.MARKDOWN.toString()
            }
            return Optional.of(send)
        }
        val hasLuckGoodLuck= luckGoodLuckService.existsByLuckRedPackIdAndUserId(redPackId,wallet.userId)
        if (hasLuckGoodLuck){
            //用户已经抢过红包
            //return Optional.empty()
        }
         //抢红包记录
        val luckGoodLuckBO= LuckGoodLuckBO().apply {
            this.luckRedPackId = redPackId
            this.userId = wallet.userId
            this.boomNumber = boomNumber.toInt()
            this.credit = null
            this.firstName=botUser.firstName
            this.lastName=botUser.lastName
            this.userName=botUser.username
            this.groupId=chatId
        }
        luckGoodLuckService.save(luckGoodLuckBO)
        //变更钱红包人数

       // input.callbackQuery.message.replyToMessage
        val fromId= input.callbackQuery.message.replyToMessage.from.id
        val firstName= input.callbackQuery.message.replyToMessage.from.firstName
        //val lastName= input.callbackQuery.message.replyToMessage.from.lastName

        //val redPack=input.callbackQuery.message.replyToMessage.text
        val addGrabNumber=counts+1
        val maxNumber=luckProperties.redPackNumbers

        val grabMessage=messageSource.getMessage("luck.grab.message",locale,maxNumber,addGrabNumber,total,boomNumber).orElse(
            LocaleHelper.EMPTY)

         val luckSendLuck= LuckSendLuckVO().apply {
             this.id=redPackId
             this.userId=sendLuckUserId
             this.credit=total.toBigDecimal()
             this.boomNumber=boomNumber.toInt()
         }
        val caption=messageSource.getMessage("luck.grab.replay",locale,firstName,fromId,total).orElse(LocaleHelper.EMPTY)
        val keyboard= InlineKeyboardMarkupHelper
            .getGrabInlineKeyboardMarkup(LocaleHelper.language(input),total,boomNumber,grabMessage,luckSendLuck,luckProperties,serviceProperties,messageSource)

        val inlineKeyboard= objectMapper.writeValueAsString(keyboard)

        val editMessageCaption=  EditMessageCaption(chatId=input.callbackQuery.message.chat.id,
            name = null,
            messageId = messageId,
            caption = caption,
            parseMode = ParseMode.MARKDOWN.toString()
        )
        editMessageCaption.replyMarkup = inlineKeyboard
        return Optional.of(editMessageCaption)
    }

}
