package com.bbbang.luck.configuration

import com.bbbang.luck.event.*
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.util.DaemonThreadFactory
import io.micronaut.context.annotation.Factory
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Factory
class DisruptorFactory {


    @Singleton
    fun disruptorFactory(divideEventHandler:DivideEventHandler):Disruptor<DivideRedPackEvent>{
        val bufferSize = 1024
        val disruptor = Disruptor<DivideRedPackEvent>(DivideRedPackEventFactory(), bufferSize, DaemonThreadFactory.INSTANCE)
        disruptor.handleEventsWith(divideEventHandler)
        return disruptor
    }


    @Singleton
    fun disruptorGrabFactory(grabEventHandler:GrabEventHandler):Disruptor<GrabRedPackEvent>{
        val bufferSize = 1024
        val disruptor = Disruptor<GrabRedPackEvent>(GrabRedPackEventFactory(), bufferSize, DaemonThreadFactory.INSTANCE)
        disruptor.handleEventsWith(grabEventHandler)
        return disruptor
    }

}