package com.bbbang.luck.service.bot.service


import com.bbbang.luck.api.bot.type.CreditLogType
import com.bbbang.luck.api.bot.type.SendLuckType
import com.bbbang.luck.configuration.properties.LuckProperties
import com.bbbang.luck.domain.po.LuckGoodLuckPO
import com.bbbang.luck.domain.po.LuckCreditLogPO
import com.bbbang.luck.domain.vo.LuckSendLuckVO
import com.bbbang.luck.event.DivideRedPackEvent
import com.bbbang.luck.repository.LuckCreditLogRepository
import com.bbbang.luck.repository.LuckGoodLuckRepository
import com.bbbang.luck.repository.LuckSendLuckRepository
import com.bbbang.luck.repository.LuckWalletRepository
import com.bbbang.luck.service.LuckCreditLogService
import com.bbbang.luck.service.LuckWalletService
import com.bbbang.luck.service.LuckGoodLuckService
import com.bbbang.luck.service.LuckUserService
import com.bbbang.parent.exception.BusinessException
import io.micronaut.chatbots.telegram.api.send.ParseMode
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import org.hibernate.SessionFactory
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


@Singleton
class DivideRedPackService (
    private val luckProperties: LuckProperties,
    private val luckWalletService: LuckWalletService,
    private val messageSource: MessageSource,
    private val luckRunningService:LuckRunningService,
    private val luckGoodLuckService: LuckGoodLuckService,
    private val luckUserService: LuckUserService,
    private val luckGoodLuckRepository: LuckGoodLuckRepository,
    private val luckSendLuckRepository: LuckSendLuckRepository,
    private val luckWalletRepository: LuckWalletRepository,
    private val luckCreditLogRepository: LuckCreditLogRepository
    ){


    fun divideRedPack(divideRedPackEvent: DivideRedPackEvent){
        //1.查询订单
        val sendRedPackVO=divideRedPackEvent.sendRedPackVO
       val redPackId=sendRedPackVO?.id ?:throw BusinessException("[redPackId]红包ID参数不能为空")
       val amount= sendRedPackVO.credit ?:throw BusinessException("[amount]金额参数不能为空")
       val numbers= sendRedPackVO.redPackNumbers ?:throw BusinessException("[numbers]红包个数不能为空")
       val oddsCredit=sendRedPackVO.odds?:throw BusinessException("[oddsCredit]保证金参数不能为空")


       val goodLuckList= luckGoodLuckService.findByLuckRedPackId(redPackId)
       val divide= luckRunningService.divide(goodLuck=goodLuckList, amount=amount,numbers=numbers)
       val boomUserList=luckUserService.findByIdInList(goodLuckList.filter { it.boomNumber==it.lastNumber }.map {goodLuck-> goodLuck.userId })
       val userWallet=  boomUserList.filter { it.inviterUserId!=null }.map {user-> user.inviterUserId }.flatMap {
            luckWalletService.findByUserIdInList(goodLuckList.map { goodLuck-> goodLuck.userId }+sendRedPackVO.userId+it)
       }
       val creditLogPOList=ArrayList<LuckCreditLogPO>()

        goodLuckList.forEach{  goodLuck ->
            //+抢到的红包金额
            val luckUsersWallet= userWallet.first { it.userId == goodLuck.userId }
            val luckLuckCreditLog= LuckCreditLogPO().apply {
                this.credit = goodLuck.credit
                this.userId = goodLuck.userId
                this.type = CreditLogType.GRAB_RED_PACK.code
                this.remark = "${CreditLogType.GRAB_RED_PACK.desc}[$redPackId]"
                this.creditBefore = luckUsersWallet.credit?.add(BigDecimal.ZERO)
                this.creditAfter = this.creditBefore?.plus(goodLuck.credit?:BigDecimal.ZERO)
                this.groupId=luckUsersWallet.groupId
            }
            //修改余额
            luckUsersWallet.credit=luckLuckCreditLog.creditAfter?.add(BigDecimal.ZERO)

            creditLogPOList.add(luckLuckCreditLog)

            if (goodLuck.boomNumber==goodLuck.lastNumber){
               //- 中雷用户赔付
                val boomLuckCreditLog=LuckCreditLogPO().apply {
                    this.credit = -oddsCredit
                    this.userId = goodLuck.userId
                    this.type = CreditLogType.BOOM.code
                    this.remark = "${CreditLogType.BOOM.desc}[$redPackId]"
                    this.creditBefore = luckUsersWallet.credit?.add(BigDecimal.ZERO)
                    this.creditAfter = this.creditBefore?.plus(this.credit?:BigDecimal.ZERO)
                    this.groupId=luckUsersWallet.groupId
                }
                //修改余额
                luckUsersWallet.credit=boomLuckCreditLog.creditAfter?.add(BigDecimal.ZERO)
                creditLogPOList.add(boomLuckCreditLog)

                //+ 我发包,中雷赔付给我
                val hostUserWallet= userWallet.first { it.userId == sendRedPackVO.userId }
                    val boomGiveMeCreditLog=LuckCreditLogPO().apply {
                        this.credit = oddsCredit
                        this.userId = hostUserWallet.userId
                        this.type = CreditLogType.COMPENSATION.code
                        this.remark = "${CreditLogType.COMPENSATION.desc}[$redPackId]"
                        this.creditBefore = hostUserWallet.credit?.add(BigDecimal.ZERO)
                        this.creditAfter = this.creditBefore?.plus(this.credit?: BigDecimal.ZERO)
                        this.groupId=hostUserWallet.groupId
                    }
                //有可能多次中雷赔付 重新给hostUserWallet赋值
                hostUserWallet.credit=boomGiveMeCreditLog.creditAfter?.add(BigDecimal.ZERO)
                    creditLogPOList.add(boomGiveMeCreditLog)

                val luckBoomPlatformWater=oddsCredit.multiply(luckProperties.platformWater).divide(BigDecimal.valueOf(100.00)).setScale(2, RoundingMode.DOWN)
                val luckBoomAgentWater=oddsCredit.multiply(luckProperties.agentWater).divide(BigDecimal.valueOf(100.00)).setScale(2, RoundingMode.DOWN)

                    //-平台抽成
                    val boomLuckCreditPlatformWater=LuckCreditLogPO().apply {
                        this.credit = -luckBoomPlatformWater
                        this.userId = hostUserWallet.userId
                        this.type = CreditLogType.PLATFORM_WATER.code
                        this.remark = "${CreditLogType.PLATFORM_WATER.desc}[$redPackId]"
                        this.creditBefore =hostUserWallet.credit?.add(BigDecimal.ZERO)
                        this.creditAfter = this.creditBefore?.plus(this.credit?: BigDecimal.ZERO)
                        this.groupId=hostUserWallet.groupId
                    }
                    hostUserWallet.credit=boomLuckCreditPlatformWater.creditAfter?.add(BigDecimal.ZERO)
                    creditLogPOList.add(boomLuckCreditPlatformWater)
                    //-代理抽成
                    val boomLuckCreditAgentWater=LuckCreditLogPO().apply {
                        this.credit = -luckBoomAgentWater
                        this.userId = hostUserWallet.userId
                        this.type = CreditLogType.AGENT_WATER.code
                        this.remark = "${CreditLogType.AGENT_WATER.desc}[$redPackId]"
                        this.creditBefore = hostUserWallet.credit?.add(BigDecimal.ZERO)
                        this.creditAfter = this.creditBefore?.plus(this.credit?: BigDecimal.ZERO)
                        this.groupId=hostUserWallet.groupId
                    }
                    hostUserWallet.credit=boomLuckCreditAgentWater.creditAfter?.add(BigDecimal.ZERO)
                    creditLogPOList.add(boomLuckCreditAgentWater)

                //+ 下级中雷分成(即 -代理抽成的金额) 1. 如果没有邀请代理，不分。
                val inviteeUserId=boomUserList.first{ boomUser-> boomUser.id==goodLuck.userId }.inviterUserId
                inviteeUserId?.let {
                    val agentWallet=userWallet.first{  it.userId==inviteeUserId}
                    val getBoomLuckCreditAgentWater=LuckCreditLogPO().apply {
                        this.credit = luckBoomAgentWater
                        this.userId = agentWallet.userId
                        this.type = CreditLogType.CHILD_BOOM_REBATE.code
                        this.remark = "${CreditLogType.CHILD_BOOM_REBATE.desc}[$redPackId]"
                        this.creditBefore = agentWallet.credit?.add(BigDecimal.ZERO)
                        this.creditAfter = this.creditBefore?.plus(this.credit?: BigDecimal.ZERO)
                        this.groupId=agentWallet.groupId
                    }
                    agentWallet.credit=getBoomLuckCreditAgentWater.creditAfter?.add(BigDecimal.ZERO)
                    creditLogPOList.add(getBoomLuckCreditAgentWater)

                }

            }
        }

        //事务更新钱包记录

        //更新分红包记录
        goodLuckList.forEach { luckUser->
            luckGoodLuckRepository.updateCreditAndLastNumberById(luckUser.id, luckUser.lastNumber, luckUser.id)
        }
        luckSendLuckRepository.updateStatusById(SendLuckType.TERMED.code,redPackId)
        //更新钱包余额
        userWallet.forEach {luckWallet->
            luckWalletRepository.updateCreditById(luckWallet.credit,luckWallet.id)
        }
        //保存明细记录
        luckCreditLogRepository.saveAll(creditLogPOList)


    }

}