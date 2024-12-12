package com.bbbang.luck.api.bot.core

interface Ordered {
    companion object {
        const  val   INIT : Int =1
        const  val   INIT_PRE_VERIFY : Int =2

        const  val   BALANCE : Int =3

        const  val   NEW_GAME : Int =4

        const  val   HELLO : Int =5
        const  val   START : Int =6
        const  val   ABOUT : Int =7

    }

}