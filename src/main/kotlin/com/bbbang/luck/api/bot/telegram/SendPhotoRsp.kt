package com.bbbang.luck.api.bot.telegram

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.InlineKeyboardMarkup
import io.micronaut.chatbots.telegram.api.User
import io.micronaut.chatbots.telegram.api.send.Send
import io.micronaut.core.annotation.NonNull
import jakarta.validation.constraints.NotNull


//  https://core.telegram.org/bots/api#editMessageCaption



 class SendPhotoRsp(
     @JsonProperty("message_id")
     val messageId: Int? = null,
     val from :User?=null,
     val chat: Chat?=null,
     val date:Int?=null,
     val caption:String?=null,
     @JsonProperty("reply_markup")
     val replyMarkup:InlineKeyboardMarkup?=null,
) {


}
