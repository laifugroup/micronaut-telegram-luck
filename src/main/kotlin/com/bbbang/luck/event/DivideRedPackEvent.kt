package com.bbbang.luck.event

import com.bbbang.luck.domain.po.LuckSendLuckPO
import com.bbbang.luck.domain.vo.LuckSendLuckVO
import io.micronaut.chatbots.telegram.api.CallbackQuery
import java.math.BigDecimal

class DivideRedPackEvent() {
    var sendRedPackVO: LuckSendLuckPO?= null
}