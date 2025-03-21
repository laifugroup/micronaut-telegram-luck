package com.bbbang.luck.service;

import com.bbbang.luck.repository.LuckActivityRepository
import com.bbbang.luck.service.LuckActivityService
import com.bbbang.luck.domain.vo.LuckActivityVO
import com.bbbang.luck.domain.bo.LuckActivityBO
import com.bbbang.luck.domain.dto.LuckActivityDTO
import com.bbbang.luck.domain.dto.LuckActivityPageDTO
import com.bbbang.luck.domain.po.LuckActivityPO
import com.bbbang.luck.domain.type.ActivityType
import com.bbbang.luck.mapper.LuckActivityMapper
import com.bbbang.parent.service2.impl.BaseServiceImpl2
import jakarta.inject.Inject
import jakarta.inject.Singleton
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal


@Singleton
open class LuckActivityService(private val repository: LuckActivityRepository) 
:BaseServiceImpl2<LuckActivityDTO, LuckActivityPageDTO, LuckActivityBO, LuckActivityPO, LuckActivityVO>(repository, LuckActivityMapper.MAPPER){

    /**
     * 新用户活动
     */
    fun findByNewUserActivity(): LuckActivityPO {
        val activityCode=ActivityType.NEW_USER_ACTIVITY.code
        val activityList=repository.findByActivityCodeOrderByIdDesc(activityCode)
        val firstActivity  = activityList.firstOrNull()
        val result=if (firstActivity?.id==null){
            LuckActivityPO(activityCode, BigDecimal.ZERO,BigDecimal.ZERO)
        }else{
            firstActivity
        }
       return result
    }

}
