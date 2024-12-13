package com.bbbang.luck.api.bot.core

interface Ordered {
    companion object {
        const  val   INIT : Int =1
        const  val   INIT_PRE_VERIFY : Int =2
        const  val   START : Int =3
        const  val   BALANCE : Int =4

        const  val   NEW_GAME : Int =5

        const  val   INVITE_LINK : Int =6

        const  val   ABOUT : Int =7

        const  val   GAME_REPORT : Int =8


        const  val   WATER_RATE : Int =9

        const  val   INVITE_QUERY : Int =10

        const  val   GRAB_RED_PACKET : Int =11



    }

}