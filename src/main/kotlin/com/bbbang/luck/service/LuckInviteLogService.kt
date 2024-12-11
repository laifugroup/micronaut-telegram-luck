package com.bbbang.luck.service;

import com.bbbang.luck.domain.vo.LuckInviteLogVO
import com.bbbang.luck.domain.bo.LuckInviteLogBO
import com.bbbang.luck.domain.dto.LuckInviteLogDTO
import com.bbbang.luck.domain.dto.LuckInviteLogPageDTO
import com.bbbang.luck.domain.po.LuckInviteLogPO
import com.bbbang.luck.domain.vo.UserInviteStatisticsVO
import com.bbbang.luck.mapper.LuckInviteLogMapper
import com.bbbang.luck.repository.LuckInviteLogRepository
import com.bbbang.parent.service2.impl.BaseServiceImpl2
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.model.Sort
import jakarta.inject.Singleton
import java.time.LocalDate


@Singleton
open class LuckInviteLogService(private val repository: LuckInviteLogRepository)
:BaseServiceImpl2<LuckInviteLogDTO, LuckInviteLogPageDTO, LuckInviteLogBO, LuckInviteLogPO, LuckInviteLogVO>(repository, LuckInviteLogMapper.MAPPER){

    fun existsByInviteeUserId(inviteeUserId:Long?):Boolean{
        return repository.existsByInviteeUserId(inviteeUserId)
    }

     fun findUserInviteStatistics(userId: Long): UserInviteStatisticsVO {
        //日合计
        val todayStart = LocalDate.now().atStartOfDay()
        val todayEnd = todayStart.plusDays(1)

        val countTodayMono = repository.countByUserIdAndCreatedAtBetween(userId, todayStart, todayEnd)
        //月合计
        val monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay()
        val monthEnd = monthStart.plusMonths(1)
        val countMonthMono = repository.countByUserIdAndCreatedAtBetween(userId, monthStart, monthEnd)
        //合计
        val totalMono=repository.countByUserId(userId)
        val top10Mono = getTop10(userId)

         return UserInviteStatisticsVO(
             currentDay = countTodayMono,
             currentMonth = countMonthMono,
             total = totalMono,
             inviteLogList = LuckInviteLogMapper.MAPPER.po2vo(top10Mono.content)
         )

    }


    private fun getTop10(userId: Long): Page<LuckInviteLogPO> {
        val sort= Sort.of(Sort.Order.desc("id"))
        val pageable= Pageable.from(0,10,sort)
        return repository.findByUserId(userId, pageable)
    }


}
