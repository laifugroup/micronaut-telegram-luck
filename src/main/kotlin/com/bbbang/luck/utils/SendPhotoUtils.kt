package com.bbbang.luck.utils

import io.micronaut.chatbots.core.SpaceParser
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.send.ParseMode
import io.micronaut.chatbots.telegram.api.send.SendPhoto
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import java.util.*

class SendPhotoUtils {
        companion object{
            fun compose(
                spaceParser: @NonNull SpaceParser<Update, Chat>,
                update: Update,
                photo: @NonNull String,
                caption:@NonNull String,
                inlineKeyboard:String,
                parseMode: @Nullable ParseMode
            ): @NonNull Optional<SendPhoto> {
                return spaceParser.parse(update)
                    .map { space: Chat? ->
                        compose(
                            space,
                            photo,
                            caption,
                            inlineKeyboard,
                            parseMode
                        )
                    }
            }

            fun compose(
                space: @NonNull Chat?,
                photo: @NonNull String,
                caption:@NonNull String,
                inlineKeyboard:String,
                parseMode: @Nullable ParseMode
            ): @NonNull SendPhoto? {
                val message = SendPhoto()
                message.photo=photo
                message.chatId = space!!.id
                message.caption=caption
                message.replyMarkup=inlineKeyboard
                message.parseMode = parseMode.toString()
                return message
            }


        }
}