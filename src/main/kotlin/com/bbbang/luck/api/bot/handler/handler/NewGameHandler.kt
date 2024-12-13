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
import io.micronaut.chatbots.telegram.api.send.ParseMode
import io.micronaut.chatbots.telegram.api.send.Send
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.api.send.SendPhoto
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import io.micronaut.context.MessageSource
import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import java.math.BigDecimal
import java.util.*

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
    override fun handle(bot: TelegramBotConfiguration?, input: Update): @NonNull Optional<Send> {

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
                this.chatId=input.message.chat.id.toString()
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
                this.chatId=input.message.chat.id.toString()
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
            this.redPackNumbers = luckProperties.redPackNumbers
            this.boomNumber = Integer.valueOf(boomNumber)
            this.credit = dollar
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


        val caption=messageSource.getMessage("luck.grab.replay",locale, UserNameHelper.getUserName(input),input.message.from.id,total).orElse(LocaleHelper.EMPTY)

        val keyboard= InlineKeyboardMarkup()
        keyboard.inlineKeyboard= listOf(
            listOf(
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.grab.message",locale,maxNumber,initGrabNumber,total,boomNumber).orElse(LocaleHelper.EMPTY)
                   // callbackData=CallbackData.GRAB_RED_PACKET
                    callbackData="${CallbackData.GRAB_RED_PACKET}|${luckSendLuck.id}|${luckSendLuck.userId}|${luckSendLuck.credit}|${luckSendLuck.boomNumber}"
                    //url=""
                }
            ),
            listOf(
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.withdrawal", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.WITHDRAWAL
                    //url=""
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.recharge", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.RECHARGE

                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.play_rule", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.PLAY_RULE
                    url=serviceProperties.playRule
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.balance", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData= CallbackData.BALANCE
                }
            ),
            listOf(
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.invite_link", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.INVITE_LINK
                    //url=""
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.invite_query", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.INVITE_QUERY
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.water_rate", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.WATER_RATE
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.game_report", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.GAME_REPORT
                }
            ),
            listOf(
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.cashier", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.CASHIER
                    url=serviceProperties.finance
                },
                InlineKeyboardButton().apply {
                    text=messageSource.getMessage("luck.callback.customer_service", LocaleHelper.language(input)).orElse(LocaleHelper.EMPTY)
                    callbackData=CallbackData.CUSTOMER_SERVICE
                    url=serviceProperties.customerService
                },
            )
        )
        val inlineKeyboard= objectMapper.writeValueAsString(keyboard)
       //SendPhotoUtils.compose(spaceParser,input,photo,caption,inlineKeyboard, ParseMode.MARKDOWN)
        return Optional.of(SendPhoto().apply {
            this.chatId = input.message.chat.id.toString()
            this.photo = luckProperties.redPackUrl
            this.caption=caption
            this.replyMarkup=inlineKeyboard
            this.parseMode=ParseMode.MARKDOWN.toString()
            this.replyToMessageId=input.message.messageId.toString()
        })
    }

}