package com.bbbang.luck.utils

import io.micronaut.chatbots.telegram.api.Update

class ChatHelper {

    companion object {
        fun getChatId(input: Update): Long? {
            val messageChatId = input.message?.chat?.id
            if (messageChatId!= null) {
                return messageChatId
            }
            val callbackQueryChatId = input.callbackQuery?.message?.chat?.id
            if (callbackQueryChatId!= null) {
                return callbackQueryChatId
            }
            return input.editedMessage?.chat?.id
        }
    }
}