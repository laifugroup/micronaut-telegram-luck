package com.bbbang.luck.utils

import com.bbbang.luck.api.bot.telegram.AnswerCallbackQuery
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.ParseMode
import io.micronaut.chatbots.telegram.api.send.SendPhoto
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import jakarta.validation.constraints.NotNull
import java.util.*

class SendAlertUtils {
        companion object{
            fun compose(
                spaceParser: @NonNull SpaceParser<Update, Chat>,
                update: Update,
                text:@Nullable String?,
                showAlert:@Nullable Boolean?,
                url: @Nullable String?,
                cacheTime: @Nullable Int?,
                ): @NonNull Optional<AnswerCallbackQuery> {
                return Optional.of(
                    compose(
                    update.message.chat,
                    update.callbackQuery.id,
                    text,
                    showAlert,
                    url,
                    cacheTime
                ))
            }

            private fun compose(
                space: @NonNull Chat?,
                callbackQueryId: @NonNull String,
                text: @NonNull String?,
                showAlert:@NonNull Boolean?,
                url:String?,
                cacheTime: @Nullable Int?,
            ):  @NonNull AnswerCallbackQuery {
                val answerCallbackQuery = AnswerCallbackQuery(callbackQueryId, text, showAlert, url, cacheTime)
                return answerCallbackQuery
            }


        }
}