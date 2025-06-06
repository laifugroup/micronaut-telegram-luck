package com.bbbang.luck

import com.bbbang.luck.api.bot.handler.command.AboutCommandHandler
import io.micronaut.chatbots.core.Dispatcher
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.Send
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.context.BeanContext
import io.micronaut.json.JsonMapper
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@MicronautTest(startApplication = false)
class AboutCommandHandlerTest {

    @Inject
    lateinit var ctx: BeanContext

    @Inject
    lateinit var dispatcher: Dispatcher<TelegramBotConfiguration, Update, Send>

    @Inject
    lateinit var jsonMapper: JsonMapper

    @Test
    fun beanOfTypeHelloWorldHandlerExists() {
        assertTrue(ctx.containsBean(AboutCommandHandler::class.java))
    }

    @Test
    @Throws(Exception::class)
    fun aboutCommandHandlerExists() {
        val send = dispatcher.dispatch(null, jsonMapper.readValue(getAboutCommandJson(), Update::class.java)).get()

        assertTrue(send is SendMessage)
        assertEquals(
            "Telegram Bot developed with 💙 and [Micronaut](https://micronaut.io)",
            (send as SendMessage).getText().trim()
        )
    }

    private fun getAboutCommandJson() = AboutCommandHandlerTest::class.java.getResource("/mockAboutCommand.json")!!.readText()
}