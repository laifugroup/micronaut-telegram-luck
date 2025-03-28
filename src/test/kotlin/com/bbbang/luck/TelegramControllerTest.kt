package com.bbbang.luck

import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TokenValidator
import io.micronaut.context.BeanContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

@MicronautTest
class TelegramControllerTest {

    @field:Client("/")
    @Inject
    lateinit var client: HttpClient

    @Inject
    lateinit var ctx: BeanContext

    @Test
    fun aboutCommandHandlerExists() {
        val token = ctx.getBean(TelegramBotConfiguration::class.java).token
        val post = HttpRequest.POST("/telegram", getAboutCommandJson())
            .header(TokenValidator.X_TELEGRAM_BOT_API_SECRET_TOKEN, token)
        val messageResponse = client.toBlocking().exchange(post, SendMessage::class.java)
        assertEquals(
            "Telegram Bot developed with 💙 and [Micronaut](https://micronaut.io)",
            messageResponse.body().text.trim()
        )
    }

    @Test
    fun tokenIsRequired() {
        val post = HttpRequest.POST("/telegram", getAboutCommandJson())
        val httpClientResponseException = assertThrows(HttpClientResponseException::class.java) {
            client.toBlocking().exchange(post, SendMessage::class.java)
        }
        assertEquals(HttpStatus.BAD_REQUEST, httpClientResponseException.status)
    }

    private fun getAboutCommandJson() = TelegramControllerTest::class.java.getResource("/mockAboutCommand.json")!!.readText()
}