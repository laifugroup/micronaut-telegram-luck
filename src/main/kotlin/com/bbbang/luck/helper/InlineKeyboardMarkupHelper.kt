package com.bbbang.luck.helper

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.configuration.properties.LuckProperties
import com.bbbang.luck.configuration.properties.ServiceProperties
import com.bbbang.luck.domain.vo.LuckSendLuckVO
import com.bbbang.luck.utils.LocaleHelper
import com.bbbang.luck.utils.UserNameHelper
import io.micronaut.chatbots.telegram.api.InlineKeyboardButton
import io.micronaut.chatbots.telegram.api.InlineKeyboardMarkup
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.context.MessageSource
import java.math.BigDecimal
import java.util.*

class InlineKeyboardMarkupHelper {

    companion object {

       fun getGrabInlineKeyboardMarkup(locale: Locale, total:String, boomNumber:String, grabMessage:String, luckSendLuck: LuckSendLuckVO, luckProperties: LuckProperties, serviceProperties: ServiceProperties, messageSource: MessageSource): InlineKeyboardMarkup {
            val keyboard= InlineKeyboardMarkup()
            keyboard.inlineKeyboard= listOf(
                listOf(
                    InlineKeyboardButton().apply {
                        text=grabMessage
                        // callbackData=CallbackData.GRAB_RED_PACKET
                        callbackData="${CallbackData.GRAB_RED_PACKET}|${luckSendLuck.id}|${luckSendLuck.userId}|${luckSendLuck.credit}|${luckSendLuck.boomNumber}"
                        //url=""
                    }
                ),
                listOf(
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.withdrawal",locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.WITHDRAWAL
                        //url=""
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.recharge", locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.RECHARGE

                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.play_rule",locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.PLAY_RULE
                        url=serviceProperties.playRule
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.balance",locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.BALANCE
                    }
                ),
                listOf(
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.invite_link", locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.INVITE_LINK
                        //url=""
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.invite_query", locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.INVITE_QUERY
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.water_rate",locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.WATER_RATE
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.game_report",locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.GAME_REPORT
                    }
                ),
                listOf(
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.cashier", locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.CASHIER
                        url=serviceProperties.finance
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.customer_service", locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.CUSTOMER_SERVICE
                        url=serviceProperties.customerService
                    },
                )
            )

            return keyboard
        }

        fun getGrabResultInlineKeyboardMarkup(locale: Locale, serviceProperties: ServiceProperties, messageSource: MessageSource): InlineKeyboardMarkup {
            val keyboard= InlineKeyboardMarkup()
            keyboard.inlineKeyboard= listOf(
                listOf(
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.withdrawal",locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.WITHDRAWAL
                        //url=""
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.recharge", locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.RECHARGE

                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.play_rule",locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.PLAY_RULE
                        url=serviceProperties.playRule
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.balance",locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.BALANCE
                    }
                ),
                listOf(
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.invite_link", locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.INVITE_LINK
                        //url=""
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.invite_query", locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.INVITE_QUERY
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.water_rate",locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.WATER_RATE
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.game_report",locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.GAME_REPORT
                    }
                ),
                listOf(
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.cashier", locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.CASHIER
                        url=serviceProperties.finance
                    },
                    InlineKeyboardButton().apply {
                        text=messageSource.getMessage("luck.callback.customer_service", locale).orElse(
                            LocaleHelper.EMPTY)
                        callbackData= CallbackData.CUSTOMER_SERVICE
                        url=serviceProperties.customerService
                    },
                )
            )

            return keyboard
        }

    }
}