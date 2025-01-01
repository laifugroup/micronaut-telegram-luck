package com.bbbang.luck.configuration

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton

@Singleton
 class LuckJob(private val luckUseCase: LuckUseCase) {

    @Scheduled(fixedDelay = "5s", initialDelay = "5s")
    fun openLuck() {
        luckUseCase.openLuck()
    }

}