package com.bbbang.luck.utils

import io.micronaut.chatbots.telegram.api.Update

class UserNameHelper {

    companion object {
        fun getUserName(input: Update): String {
            val messageFirstName = input.message?.from?.firstName
            if (messageFirstName!= null) {
                return messageFirstName
            }
            val messageLastName = input.message?.from?.lastName
            if (messageLastName!= null) {
                return messageLastName
            }

            val callbackQueryFirstName = input.callbackQuery?.from?.firstName
            if (callbackQueryFirstName!= null) {
                return callbackQueryFirstName
            }
            val callbackQueryLastName = input.callbackQuery?.from?.lastName
            if (callbackQueryLastName!= null) {
                return callbackQueryLastName
            }

            val messageUsername = input.message?.from?.username
            if (messageUsername!= null) {
                return messageUsername
            }

            val callbackQueryUsername = input.callbackQuery?.from?.username
            if (callbackQueryUsername!= null) {
                return callbackQueryUsername
            }
            return "-"
        }
    }
}