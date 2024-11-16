package com.bbbang.luck.service;

import com.bbbang.luck.api.bot.type.CreditLogType
import com.bbbang.luck.repository.LuckCreditLogRepository
import com.bbbang.luck.domain.vo.LuckCreditLogVO
import com.bbbang.luck.domain.bo.LuckCreditLogBO
import com.bbbang.luck.domain.dto.LuckCreditLogDTO
import com.bbbang.luck.domain.dto.LuckCreditLogPageDTO
import com.bbbang.luck.domain.po.LuckCreditLogPO
import com.bbbang.luck.domain.vo.AgentDaysStatisticsVO
import com.bbbang.luck.domain.vo.DaysStatisticsVO
import com.bbbang.luck.domain.vo.WaterStatisticsVO
import com.bbbang.luck.mapper.LuckCreditLogMapper
import com.bbbang.parent.service.impl.BaseServiceImpl
import com.bbbang.parent.service2.impl.BaseServiceImpl2
import jakarta.inject.Singleton
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit


@Singleton
open class LuckCreditLogService(private val repository: LuckCreditLogRepository) 
:BaseServiceImpl2<LuckCreditLogDTO, LuckCreditLogPageDTO, LuckCreditLogBO, LuckCreditLogPO, LuckCreditLogVO>(repository, LuckCreditLogMapper.MAPPER){


    /**
     * 用户级别
     */
    fun findWaterStatistics(userId:Long): WaterStatisticsVO {
        val begin = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val end=LocalDateTime.now()
        //邀请返利
        val inviteSum=repository.findSumCreditByUserIdAndTypeAndCreatedAtBetween(userId, CreditLogType.INVITE.code,begin,end)
        //下级中雷返点
        val childBoomRebateSum=repository.findSumCreditByUserIdAndTypeAndCreatedAtBetween(userId, CreditLogType.CHILD_BOOM_REBATE.code,begin,end)
        return WaterStatisticsVO(inviteSum=inviteSum,childBoomRebateSum=childBoomRebateSum)
    }

    /**
     * 用户级别
     */

    fun findGameStatistics(userId:Long): DaysStatisticsVO {
        val begin = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val end=LocalDateTime.now()
        //发包支出
        val sendSum=repository.findSumCreditByUserIdAndTypeAndCreatedAtBetween(userId, CreditLogType.SEND_RED_PACK.code,begin,end)
        //发包盈利
        val compensationSum=repository.findSumCreditByUserIdAndTypeAndCreatedAtBetween(userId, CreditLogType.COMPENSATION.code,begin,end)
        //抢包收入
        val grabSum=repository.findSumCreditByUserIdAndTypeAndCreatedAtBetween(userId, CreditLogType.GRAB_RED_PACK.code,begin,end)
        //中雷赔付
        val boomSum=repository.findSumCreditByUserIdAndTypeAndCreatedAtBetween(userId, CreditLogType.BOOM.code,begin,end)

        return DaysStatisticsVO(sendSum,compensationSum,grabSum,boomSum)
    }

    /**
     * 群组统计
     */
    fun findGameStatisticsByGroupId(yesterday:LocalDate,groupId:Long?): AgentDaysStatisticsVO {
        val startTime=LocalDateTime.of(yesterday, LocalTime.MIN)
        val endTime=LocalDateTime.of(yesterday, LocalTime.MAX)
        //发包支出
        val sendSum=repository.findSumCreditByGroupIdAndTypeAndCreatedAtBetween(groupId, CreditLogType.SEND_RED_PACK.code,startTime,endTime)
        //中雷赔付
        val boomSum=repository.findSumCreditByGroupIdAndTypeAndCreatedAtBetween(groupId, CreditLogType.BOOM.code,startTime,endTime)
        //平台抽水 2.5% 分给群组 1%
        //val waterSum=repository.findSumCreditByGroupIdAndTypeAndCreatedAtBetween(groupId, CreditLogType.PLATFORM_WATER.code,startTime,endTime)

        val agentWaterSum=(boomSum?: BigDecimal.ZERO).multiply(BigDecimal.valueOf(0.01)).setScale(2,RoundingMode.DOWN)
        return AgentDaysStatisticsVO(sendSum=sendSum,boomSum=boomSum,agentWaterSum = agentWaterSum)

    }

}
