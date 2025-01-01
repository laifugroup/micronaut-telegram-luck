package com.bbbang.luck.configuration

import com.bbbang.luck.service.LuckSendLuckService
import jakarta.inject.Singleton

@Singleton
class LuckUseCase(private val luckTask: LuckTask,private val luckSendLuckService: LuckSendLuckService) {

    fun openLuck() {
        //
        luckSendLuckService.findUnSettledLuck().forEach {
            luckTask.openLuck(it)
        }

    }

}