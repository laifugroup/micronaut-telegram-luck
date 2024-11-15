package com.bbbang.luck.utils

import io.micronaut.chatbots.telegram.api.Update
import java.util.*

class LocaleHelper {
    companion object{
        const val EMPTY="未获取"


       fun language( input: Update?):Locale{
           return  input?.message?.from?.languageCode?.let { Locale.forLanguageTag(it) } ?: Locale.ENGLISH
        }
    }
}