package com.bbbang.luck.api.bot.controller

import com.bbbang.luck.api.bot.core.CallbackData
import com.bbbang.luck.api.bot.http.entity.GetWebhookInfo
import com.bbbang.luck.api.bot.http.entity.TelegramRsp
import com.bbbang.luck.api.bot.telegram.*
import com.bbbang.luck.api.bot.type.AllowedUpdatesType
import com.bbbang.luck.configuration.LuckJob
import com.bbbang.luck.configuration.properties.BotWebHookProperties
import com.bbbang.luck.helper.InlineKeyboardMarkupHelper
import com.bbbang.parent.entities.Rsp
import com.bbbang.parent.exception.BusinessException
import com.bbbang.parent.rule.SecurityRules
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.chatbots.core.Dispatcher
import io.micronaut.chatbots.telegram.api.*
import io.micronaut.chatbots.telegram.api.send.ParseMode
import io.micronaut.chatbots.telegram.api.send.Send
import io.micronaut.chatbots.telegram.api.send.SendPhoto
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TokenValidator
import io.micronaut.chatbots.telegram.http.TelegramController
import io.micronaut.chatbots.telegram.http.TelegramControllerConfiguration
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.security.annotation.Secured
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory

/**
 * Defines a route to handle the Telegram Chatbot webhook.
 * @author Sergio del Amo
 * @since 1.0.0
 * @add 添加虚拟线程
 */
@Requires(beans = [TokenValidator::class, Dispatcher::class, TelegramBotConfiguration::class])
@Requires(
    property = TelegramControllerConfiguration.PREFIX + ".enabled",
    notEquals = StringUtils.FALSE,
    defaultValue = StringUtils.TRUE
)
@Controller("/telegramBot")
@ExecuteOn(TaskExecutors.VIRTUAL)
@Introspected
@Tag(name = "telegramBot", description = "telegramBot")
class TelegramBotController

    (private val tokenValidator: TokenValidator,
     private val dispatcher: Dispatcher<TelegramBotConfiguration, Update, Send>,
     private val telegramAPI: TelegramBotAPI,
     private val botWebHookProperties: BotWebHookProperties,
     private val objectMapper: ObjectMapper,
     private val luckJob:LuckJob
) {

    /**
     * @see [setWebhook](https://core.telegram.org/bots/api.setwebhook)
     *
     * @param apiSecretToken 请求token
     * @param update Telegram Message
     * @return  回调结果
     */
    @Operation(summary ="[新增]机器人webhook回调",
        parameters = [
            Parameter(
                name = "X-Telegram-Bot-Api-Secret-Token",
                `in` = ParameterIn.HEADER,
                example = "ceq0vd6p2ehd15jkxs62oedq55hu2qq8",
                required = true
            ),
            Parameter(
                name = "X-Real-Ip",
                `in` = ParameterIn.HEADER,
                example = "127.0.1",
                required = true
            ),
        ]
    )
    @Hidden
    @Post("/callback")
    @Secured(value = [SecurityRules.IS_ANONYMOUS])
    fun callback(
        @Header(TokenValidator.X_TELEGRAM_BOT_API_SECRET_TOKEN) apiSecretToken: String
        ,@Header("X-Real-Ip") realIp: String?
        ,@Header("X-Forwarded-For") realIp2: String?
        ,@Body update: Update
    ): HttpResponse<Send> {
        println("realIp=${realIp} Forwarded $realIp2 : in chat=${update.message?.chat?.id} with message=${update.message?.messageId}")
        val botOptional = tokenValidator.validate(apiSecretToken)
       // LOG.info("realIp=${realIp} : botName=${botOptional?.get()?.atUsername} in chat=${update?.message?.chat?.id} with message=${update?.message?.text}")
        if (botOptional.isEmpty) {
            LOG.info("无效token:${apiSecretToken}")
            return HttpResponse.unauthorized()
        }
        //存储数据 不存储，等需要存储的时候，才储存数据
        return dispatcher.dispatch(botOptional.get(), update)
            .map<MutableHttpResponse<Send>> {
                body: Send? -> HttpResponse.ok(body)
            }
            .orElseGet {
                HttpResponse.ok()
            }
    }



    @Operation(summary ="[新增]红包雨",
        parameters = [
            Parameter(
                name = "chatId",
                `in` = ParameterIn.QUERY,
                example = "-1002361396170",
                required = true
            ),
            Parameter(
                name = "message",
                `in` = ParameterIn.QUERY,
                example = "10-2",
                required = true
            ),
        ]
    )
    @Get("/redPackRain")
    @Secured(value = [SecurityRules.IS_ANONYMOUS])
    fun redPackRain(@QueryValue("chatId") chatId:Long, @QueryValue("message") message:String): HttpResponse<Send> {
        //发送消息
        val httpApiToken=botWebHookProperties.httpApiToken
        val rsp=  telegramAPI.sendMessage(httpApiToken,chatId = chatId,text = message)
        //分发消息
        val botOptional = tokenValidator.validate(botWebHookProperties.secretToken)
         return  dispatcher.dispatch(botOptional.get(), Update().apply {
                this.message = Message().apply {
                    this.messageId = rsp.result.messageId
                    this.chat = Chat().apply {
                        this.id = chatId
                    }
                    this.text = message
                    this.from = rsp.result.from
                }
             })  .map<MutableHttpResponse<Send>> {
                     body: Send? -> HttpResponse.ok(body)
             }
             .orElseGet {
                 HttpResponse.ok()
             }
    }

    @Get("/setWebHookUrl")
    @Secured(value = [SecurityRules.IS_ANONYMOUS])
    fun setWebHookUrl(): Rsp<TelegramRsp<Boolean>> {

        val allowedUpdates=listOf(
            AllowedUpdatesType.CHAT_MEMBER.code
            , AllowedUpdatesType.MESSAGE.code
            , AllowedUpdatesType.EDITED_MESSAGE.code
            , AllowedUpdatesType.CALLBACK_QUERY.code
        )
        val httpApiToken=botWebHookProperties.httpApiToken
        val webhookUrl=botWebHookProperties.url
        val secretToken=botWebHookProperties.secretToken
        //error set
        val rsp=  telegramAPI.setWebhook(httpApiToken,webhookUrl,secretToken,allowedUpdates.joinToString(","))
        return Rsp.success(rsp)
    }

    @Get("/getWebhookInfo")
    @Secured(value = [SecurityRules.IS_ANONYMOUS])
    fun getWebhookInfo(): Rsp<TelegramRsp<GetWebhookInfo>> {
        val httpApiToken=botWebHookProperties.httpApiToken
        val rsp=  telegramAPI.getWebhookInfo(httpApiToken)
        return Rsp.success(rsp)
    }

    /**
     * 只能编辑自己发送的信息。
     */

    @Get("/editMessageCaption")
    @Secured(value = [SecurityRules.IS_ANONYMOUS])
    fun editMessageCaption(): Rsp<TelegramRsp<EditMessageCaptionRsp>> {

        luckJob.openLuck()
        return Rsp.success()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(TelegramController::class.java)
    }
}
