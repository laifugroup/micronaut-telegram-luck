package com.bbbang.luck.service;

import com.bbbang.luck.repository.LuckUserRepository
import com.bbbang.luck.domain.vo.LuckUserVO
import com.bbbang.luck.domain.bo.LuckUserBO
import com.bbbang.luck.domain.dto.LuckUserDTO
import com.bbbang.luck.domain.dto.LuckUserPageDTO
import com.bbbang.luck.domain.po.LuckGoodLuckPO
import com.bbbang.luck.domain.po.LuckUserPO
import com.bbbang.luck.mapper.LuckUserMapper

import com.bbbang.parent.service2.impl.BaseServiceImpl2
import jakarta.inject.Singleton
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


@Singleton
open class LuckUserService(private val repository: LuckUserRepository) 
:BaseServiceImpl2<LuckUserDTO, LuckUserPageDTO, LuckUserBO, LuckUserPO, LuckUserVO>(repository, LuckUserMapper.MAPPER){

    fun findByBotUserId(botUserId: Long,groupId:Long?): LuckUserVO? {
       val user= repository.findByBotUserIdAndGroupId(botUserId,groupId!!)
        return if (user!=null){
            LuckUserMapper.MAPPER.po2vo(user)
        }else{
            null
        }
    }


    fun findByIdInList(idList: List<Long?>): List<LuckUserPO>{
        return  repository.findByIdInList(idList)
    }

    fun yesterdayUsers(yesterday:LocalDate,groupId: Long?):Int{
        val startTime=LocalDateTime.of(yesterday, LocalTime.MIN)
        val endTime=LocalDateTime.of(yesterday, LocalTime.MAX)
        return repository.countIdByGroupIdAndCreatedAtBetween(groupId, startTime =startTime,endTime=endTime )
    }

}
