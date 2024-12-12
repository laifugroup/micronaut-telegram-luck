package com.bbbang.luck.api.bot.http.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import io.swagger.v3.oas.annotations.media.Schema


//{"ok":true,"result":true,"description":"Webhook was set"}
@Schema()
@Introspected
data class TelegramRsp<T>(val ok:Boolean,val result:T, val description:String?) {
}


@Schema()
@Introspected
data class GetWebhookInfo(val url:String, @JsonProperty("ip_address")val  ipAddress:String?
                    ,@JsonProperty("max_connections")val maxConnections:Int?
                    ,@JsonProperty("allowed_updates") val allowedUpdates:List<String>?) {
}


@Schema()
@Introspected
data class ChatInviteLink(val url:String, @JsonProperty("chat_id")val  chatId:Long?
                          ,@JsonProperty("name")val name:String?
                          ,@JsonProperty("expire_date")val expireDate:Int?
                          ,@JsonProperty("member_limit") val memberLimit:Int
                          ,@JsonProperty("creates_join_request")val createsJoinRequest:Boolean?) {
}
