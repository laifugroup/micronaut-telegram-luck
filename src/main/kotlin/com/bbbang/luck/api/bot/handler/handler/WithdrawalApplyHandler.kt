package com.bbbang.luck.api.bot.handler.handler

import com.bbbang.luck.api.bot.core.Ordered
import com.bbbang.luck.api.bot.handler.handler.RechargeApplyHandler.Companion.RECHARGE_APPLY
import com.bbbang.luck.api.bot.type.CreditLogType
import com.bbbang.luck.domain.bo.LuckCreditLogBO
import com.bbbang.luck.service.LuckCreditLogService
import com.bbbang.luck.service.LuckWalletService
import com.bbbang.luck.utils.LocaleHelper
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.ParseMode
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TelegramHandler
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import java.math.BigDecimal
import java.util.*

/**
 * 下分提现申请
 */
@Singleton
open class WithdrawalApplyHandler(private val spaceParser: SpaceParser<Update, Chat>
                                  , private val messageSource: MessageSource
                                  , private val luckWalletService: LuckWalletService
                                  ,private val luckCreditLogService: LuckCreditLogService
    ) : TelegramHandler<SendMessage> {

    companion object{
        const val  WITHDRAWAL_APPLY= "下分(\\d+(\\.\\d+)?)"
    }
    override fun getOrder() = Ordered.WITHDRAWAL_APPLY

    override fun canHandle(bot: TelegramBotConfiguration?, input: Update): Boolean {
        println("---withdrawalApply")
        val match=  input.message?.text?.matches(WITHDRAWAL_APPLY.toRegex())
        return  match!=null && match
    }

    override fun handle(bot: TelegramBotConfiguration?, input: Update): Optional<SendMessage>{
        //截取参数
        val chat= spaceParser.parse(input)
        //正则获取数字
        val  minCredit= -input.message.text.replace(WITHDRAWAL_APPLY.toRegex(),"$1").toBigDecimal()

        //获取余额
        val wallet= luckWalletService.findWalletByUserId(input.message?.from?.id,input.message?.chat?.id)
        val userId=wallet.userId

        //更新余额
        luckWalletService.updateCreditById(minCredit,wallet.id)
        val luckCreditLogBO= LuckCreditLogBO().apply {
            this.credit = minCredit
            this.userId = input.message?.from?.id
            this.type = CreditLogType.CREDIT_UP_PASS.code
            this.remark = "${CreditLogType.CREDIT_UP_PASS.desc}[${userId}]"
            this.creditBefore = wallet.credit
            this.creditAfter = this.creditBefore?.plus(this.credit?: BigDecimal.ZERO)
            this.groupId=chat.get().id
        }
        //更新明细
        luckCreditLogService.save(luckCreditLogBO)
        //
        val luckBalance = messageSource.getMessage("luck.balance", LocaleHelper.language(input),userId,luckCreditLogBO.creditAfter)
            .orElse(LocaleHelper.EMPTY)

        // val sendMessage=   SendMessageUtils.compose(spaceParser,input,luckBalance,ParseMode.MARKDOWN)
        val sendMessage =  SendMessage().apply {
            chatId = chat.get()?.id.toString()
            replyToMessageId=input.message?.messageId?.toString()
            text=luckBalance
            parseMode=ParseMode.MARKDOWN.toString()
        }
        return  Optional.of(sendMessage)
    }


}
