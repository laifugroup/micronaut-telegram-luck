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
                    ,@JsonProperty("has_custom_certificate")val hasCustomCertificate:Boolean?
                    ,@JsonProperty("pending_update_count")val pendingUpdateCount:Int?
                    ,@JsonProperty("allowed_updates") val allowedUpdates:List<String>?) {
}
