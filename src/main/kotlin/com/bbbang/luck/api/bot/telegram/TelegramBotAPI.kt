package com.bbbang.luck.api.bot.telegram

import com.bbbang.luck.api.bot.http.entity.GetWebhookInfo
import com.bbbang.luck.api.bot.http.entity.TelegramRsp
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.chatbots.telegram.api.ChatInviteLink
import io.micronaut.chatbots.telegram.api.InlineKeyboardMarkup
import io.micronaut.chatbots.telegram.api.send.SendMessage
import io.micronaut.chatbots.telegram.api.send.SendPhoto
import io.micronaut.chatbots.telegram.api.send.SendText
import io.micronaut.context.annotation.Parameter
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.async.annotation.SingleResult
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.annotation.RequestBean
import io.micronaut.http.client.annotation.Client
import jakarta.validation.constraints.NotNull

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

    @Post("/bot{httpApiToken}/createChatInviteLink")
    @SingleResult
    fun createChatInviteLink(@PathVariable("httpApiToken")httpApiToken:String,
        @QueryValue("chat_id") chatId:Long?
        ,@QueryValue("name") name:String?
        ,@QueryValue("expire_date") expireDate:Int?
        ,@QueryValue("member_limit") memberLimit:Int?
        ,@QueryValue("creates_join_request") createsJoinRequest:Boolean?,
    )
    : TelegramRsp<ChatInviteLink>

    /**
     * 发送红包雨的时候使用
     */

    @Post("/bot{httpApiToken}/sendPhoto")
    @SingleResult
    fun sendPhoto(@PathVariable("httpApiToken")httpApiToken:String
                  ,@QueryValue("chat_id") chatId:Long?
                  , @QueryValue("photo") photo:String?
                  , @QueryValue("caption") caption:String?
                  , @QueryValue("parse_mode") parseMode:String?
                  , @QueryValue("reply_markup") replyMarkup: String?,
    )
    : TelegramRsp<SendPhotoRsp>


    @Post("/bot{httpApiToken}/sendMessage")
    @SingleResult
    fun sendMessage(@PathVariable("httpApiToken")httpApiToken:String
        ,@QueryValue("chat_id") chatId:Long
        , @QueryValue("text") text:String
    ): TelegramRsp<SendPhotoRsp>

    /**
     * 开奖的时候使用
     */
    @Post("/bot{httpApiToken}/editMessageCaption")
    @SingleResult
    fun editMessageCaption(@PathVariable("httpApiToken")httpApiToken:String,
                           @QueryValue("chat_id")
                            chatId: Long,
                           @QueryValue("message_id")
                            messageId: Int,
                           @QueryValue("caption")
                            caption: String? = null,
                           @QueryValue("parse_mode")
                            parseMode: String? = null,
                           @QueryValue("reply_markup")
                           replyMarkup: String? = null,
    )

}


