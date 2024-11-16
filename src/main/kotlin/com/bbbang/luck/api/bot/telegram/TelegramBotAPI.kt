package com.bbbang.luck.api.bot.telegram

import com.bbbang.luck.api.bot.http.entity.GetWebhookInfo
import com.bbbang.luck.api.bot.http.entity.TelegramRsp
import io.micronaut.core.async.annotation.SingleResult
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

/**
 *
 */
@Client(id = "telegram")
//@Headers(
//    Header(name = USER_AGENT, value = "Micronaut HTTP Client"),
//    Header(name = ACCEPT, value = "application/vnd.github.v3+json, application/json")
//)
interface TelegramBotAPI {


    @Post("/bot{httpApiToken}/setWebhook")
    @SingleResult
    fun setWebhook(@PathVariable("httpApiToken")httpApiToken:String
                   ,@QueryValue("url") webhookUrl:String
                   ,@QueryValue("secret_token")secretToken:String
                   ,@QueryValue("allowed_updates")allowedUpdates:String)
    : TelegramRsp<Boolean>

    @Post("/bot{httpApiToken}/getWebhookInfo")
    @SingleResult
    fun getWebhookInfo(@PathVariable("httpApiToken")httpApiToken:String)
    : TelegramRsp<GetWebhookInfo>




}


