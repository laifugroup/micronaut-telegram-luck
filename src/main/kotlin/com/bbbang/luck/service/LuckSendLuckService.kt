package com.bbbang.luck.service;

import com.bbbang.luck.api.bot.type.SendLuckType
import com.bbbang.luck.repository.LuckSendLuckRepository
import com.bbbang.luck.domain.vo.LuckSendLuckVO
import com.bbbang.luck.domain.bo.LuckSendLuckBO
import com.bbbang.luck.domain.dto.LuckSendLuckDTO
import com.bbbang.luck.domain.dto.LuckSendLuckPageDTO
import com.bbbang.luck.domain.po.LuckSendLuckPO
import com.bbbang.luck.mapper.LuckSendLuckMapper
import com.bbbang.parent.service.impl.BaseServiceImpl
import com.bbbang.parent.service2.impl.BaseServiceImpl2
import jakarta.inject.Singleton


@Singleton
open class LuckSendLuckService(private val repository: LuckSendLuckRepository) 
:BaseServiceImpl2<LuckSendLuckDTO, LuckSendLuckPageDTO, LuckSendLuckBO, LuckSendLuckPO, LuckSendLuckVO>(repository, LuckSendLuckMapper.MAPPER){

    fun findUnSettledLuck():List<LuckSendLuckPO>{
        return repository.findByStatusEquals(SendLuckType.UNSETTLED.code)
    }


}
