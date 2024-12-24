package com.bbbang.luck.event

import com.bbbang.luck.domain.vo.LuckSendLuckVO
import io.micronaut.chatbots.telegram.api.CallbackQuery
import java.math.BigDecimal

class DivideRedPackEvent() {
    var sendRedPackVO: LuckSendLuckVO?= null
    var callbackQuery: CallbackQuery?=null
    var oddsCredit:BigDecimal?=null
}