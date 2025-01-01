package com.bbbang.luck.configuration

import com.bbbang.luck.domain.po.LuckSendLuckPO
import com.bbbang.luck.event.DivideRedPackEvent
import com.lmax.disruptor.EventTranslator
import com.lmax.disruptor.dsl.Disruptor
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton

@Singleton
class LuckTask(private val disruptor: Disruptor<DivideRedPackEvent>) {

    fun openLuck(luckSendLuckPO: LuckSendLuckPO) {
        disruptor.publishEvent(EventTranslator<DivideRedPackEvent> { event, _ ->
            event.sendRedPackVO=luckSendLuckPO
        })
    }

}