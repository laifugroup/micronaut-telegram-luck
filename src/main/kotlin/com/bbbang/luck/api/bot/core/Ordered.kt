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


        const  val   WITHDRAWAL : Int =12


        const  val   RECHARGE : Int =13


        const  val  RECHARGE_APPLY :Int = 14

        const  val  WITHDRAWAL_APPLY :Int = 15

    }

}