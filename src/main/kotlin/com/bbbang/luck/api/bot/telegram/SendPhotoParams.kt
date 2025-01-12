package com.bbbang.luck.api.bot.telegram

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.context.annotation.Parameter
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.QueryValue
import io.swagger.v3.oas.annotations.media.Schema

@Schema()
@Introspected
data class SendPhotoParams(
    @field:Schema(description = "chat_id",type="string")
    @JsonProperty("chat_id")
    val chatId:Long?=null
    ,val photo:String?=null
    ,val caption:String?=null
    ,@JsonProperty("parse_mode")val parseMode:String?=null
    ,@JsonProperty("reply_markup") val replyMarkup: String?=null
) {


}