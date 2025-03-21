package com.bbbang.luck.configuration.bot

import TelegramSpaceParserExt
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton


@Factory
open class SpaceParserConfiguration {

    @Singleton
    fun getSpaceParser(): SpaceParser<Update, Chat> {
        return TelegramSpaceParserExt()
    }
}