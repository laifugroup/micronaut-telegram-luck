package com.bbbang.luck.api.bot.handler.handler

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.type.CreditLogType
import com.bbbang.luck.api.bot.type.SendLuckType
import com.bbbang.luck.configuration.properties.LuckProperties
import com.bbbang.luck.configuration.properties.ServiceProperties
import com.bbbang.luck.configuration.properties.TronProperties
import com.bbbang.luck.domain.bo.LuckCreditLogBO
import com.bbbang.luck.domain.bo.LuckSendLuckBO
import com.bbbang.luck.domain.po.LuckSendLuckPO
import com.bbbang.luck.helper.InlineKeyboardMarkupHelper
import com.bbbang.luck.service.LuckCreditLogService
import com.bbbang.luck.service.LuckSendLuckService
import com.bbbang.luck.service.LuckWalletService
import com.bbbang.luck.utils.LocaleHelper
import com.bbbang.luck.utils.SendPhotoUtils
import com.bbbang.luck.utils.UserNameHelper
import com.bbbang.parent.utils.BigDecimalUtils
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.InlineKeyboardButton
import io.micronaut.chatbots.telegram.api.InlineKeyboardMarkup
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.*
import io.micronaut.chatbots.telegram.core.SendMessageUtils
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import io.micronaut.context.MessageSource
import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import java.math.BigDecimal
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Singleton
open class NewGameHandler(private val spaceParser: SpaceParser<Update, Chat>,
                          private val objectMapper: ObjectMapper,
                          private val messageSource: MessageSource,
                          private val luckProperties: LuckProperties,
                          private val luckWallService: LuckWalletService,
                          private val luckSendLuckService: LuckSendLuckService,
                          private val luckCreditLogService: LuckCreditLogService,
                          private val tronProperties: TronProperties,
                          private val serviceProperties: ServiceProperties,
                     ) : TelegramHandler<Send> {

    companion object{
        const  val SEND_LUCK = "^\\d+[-|/]\\d$"
    }

    override fun getOrder() = Ordered.NEW_GAME

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("---newGame")
        val match=  input.message?.text?.matches(SEND_LUCK.toRegex())
        return match!=null && match
    }

    @Transactional
    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<Send> {
        //不允许机器人
        if (input?.message?.from?.bot == true) {
            return Optional.empty()
        }
        //不允许私聊中执行指令
        if (input?.message?.from?.id == input?.message?.chat?.id) {
            val privateChatCommandMessage =
                messageSource.getMessage("private.chat.bot.command", LocaleHelper.language(input))
                    .orElse(LocaleHelper.EMPTY)
            return SendMessageUtils.compose(spaceParser, input, privateChatCommandMessage) as Optional<Send>
        }

        val chat= spaceParser.parse(input)
        //业务逻辑
        val (total, boomNumber) = input.message.text.split("[-|/]".toRegex()).take(2)
        //限制发红包金额
        val dollar = BigDecimal.valueOf(total.toDouble())
        if (dollar.compareTo(luckProperties.reservePrice)==BigDecimalUtils.SMALL ||
            dollar.compareTo(luckProperties.limitBid)== BigDecimalUtils.BIG
        ) {
            val luckLimitAmount = messageSource.getMessage("luck.limit.amount", LocaleHelper.language(input),luckProperties.reservePrice,luckProperties.limitBid)
                .orElse(LocaleHelper.EMPTY)
            val sendMessage=SendMessage().apply {
                this.text=luckLimitAmount
                this.chatId=chat.getOrNull()?.id?.toString()
                this.replyToMessageId=input.message.messageId.toString()
                this.parseMode=ParseMode.MARKDOWN.toString()
            }
            return Optional.of(sendMessage)
        }
        //验证余额是否足够
        val wallet=luckWallService.findWalletByUserId(input.message.from.id,input.message.chat.id)
        if (wallet.credit?.compareTo(dollar)==BigDecimalUtils.SMALL) {
            val luckInsufficientBalance = messageSource.getMessage("luck.insufficient.balance", LocaleHelper.language(input),
                input.message?.from?.firstName,input.message?.from?.id,wallet.credit,tronProperties.rechargeAddress).orElse(LocaleHelper.EMPTY)
            val sendMessage=SendMessage().apply {
                this.text=luckInsufficientBalance
                this.chatId=chat.getOrNull()?.id.toString()
                this.replyToMessageId=input.message.messageId.toString()
                this.parseMode=ParseMode.MARKDOWN.toString()
            }
            return Optional.of(sendMessage)
        }

        //新余额
        val balance = wallet.credit?.minus(dollar)

        val counts=luckWallService.updateCreditById(balance,wallet.id)
       // println("counts=${counts}")
        val sendLuckPO= LuckSendLuckBO().apply {
            this.userId = wallet.userId
            this.messageId=input.message?.messageId?.toLong()?.plus(1)//仅仅用于测试
            this.redPackNumbers = luckProperties.redPackNumbers
            this.boomNumber = Integer.valueOf(boomNumber)
            this.credit = dollar
            this.odds = luckProperties.odds
            this.firstName=input.message.from.firstName
            this.lastName=input.message.from.lastName
            this.userName=input.message.from.username
            this.groupId=input.message.chat.id
            this.status= SendLuckType.UNSETTLED.code
        }

       val luckSendLuck= luckSendLuckService.save(sendLuckPO)
       // println("luckSendLuck=${luckSendLuck.id}")

        val luckCreditLogBO= LuckCreditLogBO().apply {
            this.credit = -dollar
            this.userId = wallet.userId
            this.type = CreditLogType.SEND_RED_PACK.code
            this.remark = "${CreditLogType.SEND_RED_PACK.desc}[${input.message.text}]"
            this.creditBefore = wallet.credit
            this.creditAfter = balance
            this.groupId=wallet.groupId
        }

       val luckCreditLog= luckCreditLogService.save(luckCreditLogBO)
      //  println("luckCreditLog=${luckCreditLog.id}")
        val locale= LocaleHelper.language(input)
        val maxNumber=luckProperties.redPackNumbers
        val initGrabNumber=0

        val caption=messageSource.getMessage("luck.grab.replay",locale, UserNameHelper.getUserName(input),input.message.from.id,total).orElse(
            LocaleHelper.EMPTY)

       val grabMessage= messageSource.getMessage("luck.grab.message",locale,maxNumber,initGrabNumber,total,boomNumber).orElse(
            LocaleHelper.EMPTY)

        val keyboard=InlineKeyboardMarkupHelper
            .getGrabInlineKeyboardMarkup(locale,total,boomNumber,grabMessage,luckSendLuck,luckProperties,serviceProperties,messageSource)

        val inlineKeyboard= objectMapper.writeValueAsString(keyboard)
       //SendPhotoUtils.compose(spaceParser,input,photo,caption,inlineKeyboard, ParseMode.MARKDOWN)
        return Optional.of(SendPhoto().apply {
            this.chatId=chat.getOrNull()?.id?.toString()
            this.photo = luckProperties.redPackUrl
            this.caption=caption
            this.replyMarkup=inlineKeyboard
            this.parseMode=ParseMode.MARKDOWN.toString()
            this.replyToMessageId=input.message.messageId.toString()
        })
    }

}