package com.bbbang.luck.helper

import io.micronaut.chatbots.telegram.api.User


object SassIdHelper {


    fun getSassId(groupId:Long?,botUserId:Long?):String{
        return "${groupId}_${botUserId}"
    }

    fun getSassId(groupId:Long?,botUser:User?):String{
        return "${groupId}_${botUser?.id}"
    }


}