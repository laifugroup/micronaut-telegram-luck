package com.bbbang.luck.api.bot.telegram

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.chatbots.telegram.api.send.Send
import io.micronaut.core.annotation.NonNull
import jakarta.validation.constraints.NotNull


//  https://core.telegram.org/bots/api#answercallbackquery



 class AnswerCallbackQuery(
    @JsonProperty("callback_query_id")
    @NotNull
    @NonNull
    val callbackQueryId: String,
    val text: String?,
    @JsonProperty("show_alert")
    val showAlert: Boolean? = true,
    val url: String? = null,
    @JsonProperty("cache_time")
    val cacheTime: Int? = null,
     method: String?=SEND_ANSWER_CALLBACK_QUERY,
): Send(method) {
     companion object{
         const val SEND_ANSWER_CALLBACK_QUERY = "answercallbackquery"
     }

}

//@JsonProperty("chat_id")
//private val chatId: @NotNull @NonNull Any? = null