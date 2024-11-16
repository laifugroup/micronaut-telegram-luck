package com.bbbang.luck.service

import com.bbbang.luck.domain.vo.ReportAnalysisDailyVO
import jakarta.inject.Singleton
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Singleton
class ReportService(private val luckUserService: LuckUserService,
                    private val luckCreditLogService: LuckCreditLogService) {

    fun getGroupReport(groupId:Long?): ReportAnalysisDailyVO {
        val yesterday=LocalDate.now().minusDays(1)
        //用户
        val user= luckUserService.yesterdayUsers(yesterday,groupId)
        val agentDaysStatistics=  luckCreditLogService.findGameStatisticsByGroupId(yesterday,groupId)

       return ReportAnalysisDailyVO(analysisDate = LocalDateTime.of(yesterday, LocalTime.MIN),
            userCounts = user,
            gameCounts=agentDaysStatistics.sendSum?:BigDecimal.ZERO,
            boomCounts = agentDaysStatistics.boomSum?: BigDecimal.ZERO,
            profitCounts =agentDaysStatistics.agentWaterSum?: BigDecimal.ZERO
        )
    }

}