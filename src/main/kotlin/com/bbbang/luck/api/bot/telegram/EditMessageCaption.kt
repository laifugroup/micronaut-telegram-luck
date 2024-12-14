package com.bbbang.luck.api.bot.telegram

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.chatbots.telegram.api.InlineKeyboardMarkup
import io.micronaut.chatbots.telegram.api.send.Send
import io.micronaut.core.annotation.NonNull
import jakarta.validation.constraints.NotNull


//  https://core.telegram.org/bots/api#editMessageCaption



 class EditMessageCaption(
     @JsonProperty("business_connection_id")
     @NotNull
     @NonNull
    val businessConnectionId: String?=null,
     @JsonProperty("chat_id")
     @NotNull
     @NonNull
     var chatId: Long?,
     val name: String?,
     @JsonProperty("message_id")
    val messageId: Int? = null,
     @JsonProperty("inline_message_id")
    val inlineMessageId: Int?=null,
     val caption: String? = null,
     @JsonProperty("parse_mode")
     val parseMode: String? = null,
     @JsonProperty("caption_entities")
     val captionEntities: String? = null,
     method: String?=CREATE_CHAT_INVITE_LINK,
): Send(method) {
     companion object{
         const val CREATE_CHAT_INVITE_LINK = "editMessageCaption"
     }

}
