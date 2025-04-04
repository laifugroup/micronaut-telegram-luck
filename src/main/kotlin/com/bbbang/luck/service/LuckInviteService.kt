package com.bbbang.luck.service;

import com.bbbang.luck.domain.bo.LuckInviteBO
import com.bbbang.luck.domain.dto.LuckInviteDTO
import com.bbbang.luck.domain.dto.LuckInvitePageDTO
import com.bbbang.luck.domain.po.LuckInvitePO
import com.bbbang.luck.domain.vo.LuckInviteVO
import com.bbbang.luck.mapper.LuckInviteMapper
import com.bbbang.luck.repository.LuckInviteRepository
import com.bbbang.parent.service2.impl.BaseServiceImpl2
import jakarta.inject.Singleton
import reactor.core.publisher.Mono


@Singleton
open class LuckInviteService(private val repository: LuckInviteRepository)
:BaseServiceImpl2<LuckInviteDTO, LuckInvitePageDTO, LuckInviteBO, LuckInvitePO, LuckInviteVO>(repository, LuckInviteMapper.MAPPER){

    fun findByUserId(userId:Long):LuckInviteVO?{
        val result=repository.findOneByUserIdOrderByIdDesc(userId)
        return  if (result==null){
             null
        }else{
             LuckInviteMapper.MAPPER.po2vo(result)
        }
    }

    fun findByUrlHash(urlHash:String):LuckInviteVO?{
        val result=repository.findByUrlHash(urlHash)
        return  if (result==null){
            null
        }else{
            LuckInviteMapper.MAPPER.po2vo(result)
        }
    }

}
