package com.bbbang.luck.api.bot.core

 interface CallbackData {
   companion object{
       //红包
       const val  GRAB_RED_PACKET : String ="grab"

       //提现
       const val  WITHDRAWAL: String ="withdrawal"
       //充值
       const val RECHARGE : String ="recharge"
       //玩法
       const val  PLAY_RULE : String ="play_rule"
       //余额
        const val  BALANCE : String ="balance"

       //推广链接
       const val INVITE_LINK: String ="invite_link"
       //推广查询
       const val INVITE_QUERY: String ="invite_query"


       //反水统计
       const val  WATER_RATE : String ="water_rate"
       //统计报表
       const val  GAME_REPORT : String ="game_report"


       //财务
       const val  CASHIER : String ="cashier"

       //客服
       const val  CUSTOMER_SERVICE : String ="customer_service"






   }
}