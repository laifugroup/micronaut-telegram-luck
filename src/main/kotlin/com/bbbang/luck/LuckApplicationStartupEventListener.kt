package com.bbbang.luck


import com.bbbang.luck.api.bot.telegram.TelegramBotAPI
import com.bbbang.luck.api.bot.type.AllowedUpdatesType
import com.bbbang.parent.helper.LicenseHelper
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.system.exitProcess

@Singleton
open class LuckApplicationStartupEventListener: ApplicationEventListener<ServerStartupEvent> {

    @Inject
    lateinit var telegramBotAPI: TelegramBotAPI

    @Inject
    lateinit var objectMapper: ObjectMapper

    override fun onApplicationEvent(event: ServerStartupEvent?) {
        //registerLicense()
       registerTelegram()
    }

    private fun registerLicense(){
        //开源版本屏蔽证书验证
        try {
            LicenseHelper.verifyLicenseStartUp()
        }catch (e:Exception){
            println("[证书]许可已到期或不正确,请向服务商获取新证书。")
            exitProcess(-1)
        }
    }
    private fun registerTelegram(){
        try{
            val allowedUpdates=listOf(
                AllowedUpdatesType.CHAT_MEMBER.code
                , AllowedUpdatesType.MESSAGE.code
                , AllowedUpdatesType.EDITED_MESSAGE.code
                , AllowedUpdatesType.CALLBACK_QUERY.code
            )

            // ["chat_member","message","edited_message","callback_query"]
    //     https://ed29-182-139-189-174.ngrok-free.app
            val httpApiToken="6968916542:AAFseuM2BiI1WhPI5YvIR32CTMyqYU6qyfU"
            val host="https://056b-182-139-189-174.ngrok-free.app"
            val webhookUrl="$host/telegramBot/callback"
            val secretToken="zYCn88NYyzsjG9QGd8626BMGQ5y7DFBc"
            val joinAllowedUpdates=objectMapper.writeValueAsString(allowedUpdates)
            val register=  telegramBotAPI.setWebhook(httpApiToken,webhookUrl,secretToken,joinAllowedUpdates)
            println(register.description)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }



}