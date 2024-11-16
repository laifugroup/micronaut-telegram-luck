package com.bbbang.luck.service;

import com.bbbang.luck.repository.LuckGoodLuckRepository
import com.bbbang.luck.domain.vo.LuckGoodLuckVO
import com.bbbang.luck.domain.bo.LuckGoodLuckBO
import com.bbbang.luck.domain.dto.LuckGoodLuckDTO
import com.bbbang.luck.domain.dto.LuckGoodLuckPageDTO
import com.bbbang.luck.domain.po.LuckGoodLuckPO
import com.bbbang.luck.mapper.LuckGoodLuckMapper
import com.bbbang.parent.service.impl.BaseServiceImpl
import com.bbbang.parent.service2.impl.BaseServiceImpl2
import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import reactor.core.publisher.Mono


@Singleton
open class LuckGoodLuckService(private val repository: LuckGoodLuckRepository) 
:BaseServiceImpl2<LuckGoodLuckDTO, LuckGoodLuckPageDTO, LuckGoodLuckBO, LuckGoodLuckPO, LuckGoodLuckVO>(repository, LuckGoodLuckMapper.MAPPER){


    fun existsByLuckRedPackIdAndUserId(@NonNull luckRedPackId: Long?, @NonNull userId: Long?): Boolean{
        return repository.existsByLuckRedPackIdAndUserId(luckRedPackId, userId)
    }

    fun countByLuckRedPackId(luckRedPackId: Long?): Int{
        return repository.countByLuckRedPackId(luckRedPackId)
    }

    fun findByLuckRedPackId(luckRedPackId: Long?): List<LuckGoodLuckPO>{
        return repository.findByLuckRedPackId(luckRedPackId)
    }
}
