package com.bbbang.luck.api.bot.controller

import com.bbbang.luck.api.bot.telegram.TelegramBotAPI
import com.bbbang.luck.api.bot.http.entity.GetWebhookInfo
import com.bbbang.luck.api.bot.http.entity.TelegramRsp
import com.bbbang.luck.api.bot.type.AllowedUpdatesType
import com.bbbang.parent.entities.Rsp
import com.bbbang.parent.rule.SecurityRules
import io.micronaut.chatbots.core.Dispatcher
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.Send
import io.micronaut.chatbots.telegram.core.TelegramBotConfiguration
import io.micronaut.chatbots.telegram.core.TokenValidator
import io.micronaut.chatbots.telegram.http.TelegramController
import io.micronaut.chatbots.telegram.http.TelegramControllerConfiguration
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
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
     private val telegramAPI: TelegramBotAPI
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
        @Header(TokenValidator.X_TELEGRAM_BOT_API_SECRET_TOKEN) apiSecretToken: String?,
        @Body update: Update
    ): HttpResponse<Send> {
        LOG.info("------------------:callback")
        val botOptional = tokenValidator.validate(apiSecretToken)
        if (botOptional.isEmpty) {
            LOG.trace("无效token:${apiSecretToken}")
            return HttpResponse.unauthorized()
        }
        return dispatcher.dispatch(botOptional.get(), update)
            .map<MutableHttpResponse<Send>> { body: Send? -> HttpResponse.ok(body) }
            .orElseGet { HttpResponse.ok() }
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

        val httpApiToken="6968916542:AAFseuM2BiI1WhPI5YvIR32CTMyqYU6qyfU"
        val webhookUrl="https://106f-2409-8a62-f11-d5b0-8c54-86e-9e23-4dbe.ngrok-free.app/telegramBot/callback"
        val secretToken="zYCn88NYyzsjG9QGd8626BMGQ5y7DFBc"
        //error set
        val rsp=  telegramAPI.setWebhook(httpApiToken,webhookUrl,secretToken,allowedUpdates.joinToString(","))
      return Rsp.success(rsp)
    }

    @Get("/getWebhookInfo")
    @Secured(value = [SecurityRules.IS_ANONYMOUS])
    fun getWebhookInfo(): Rsp<TelegramRsp<GetWebhookInfo>> {
        val httpApiToken="6968916542:AAFseuM2BiI1WhPI5YvIR32CTMyqYU6qyfU"
        val rsp=  telegramAPI.getWebhookInfo(httpApiToken)
        return Rsp.success(rsp)
    }




    companion object {
        private val LOG = LoggerFactory.getLogger(TelegramController::class.java)
    }
}
