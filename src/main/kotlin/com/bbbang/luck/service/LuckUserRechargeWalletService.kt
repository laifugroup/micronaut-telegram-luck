package com.bbbang.luck.service;

import com.bbbang.luck.repository.LuckUserRechargeWalletRepository
import com.bbbang.luck.service.LuckUserRechargeWalletService
import com.bbbang.luck.domain.vo.LuckUserRechargeWalletVO
import com.bbbang.luck.domain.bo.LuckUserRechargeWalletBO
import com.bbbang.luck.domain.dto.LuckUserRechargeWalletDTO
import com.bbbang.luck.domain.dto.LuckUserRechargeWalletPageDTO
import com.bbbang.luck.domain.po.LuckUserRechargeWalletPO
import com.bbbang.luck.domain.type.UserRechargeWalletType
import com.bbbang.luck.mapper.LuckUserRechargeWalletMapper
import com.bbbang.parent.service.impl.BaseServiceImpl
import com.bbbang.parent.service2.impl.BaseServiceImpl2
import jakarta.inject.Inject
import jakarta.inject.Singleton
import reactor.core.publisher.Mono


@Singleton
open class LuckUserRechargeWalletService(private val repository: LuckUserRechargeWalletRepository) 
:BaseServiceImpl2<LuckUserRechargeWalletDTO, LuckUserRechargeWalletPageDTO, LuckUserRechargeWalletBO, LuckUserRechargeWalletPO, LuckUserRechargeWalletVO>(repository, LuckUserRechargeWalletMapper.MAPPER){


    fun findByUserId(userId:Long?,groupId:Long?): LuckUserRechargeWalletVO {
        val user = repository.findByUserId(userId)
        val actualUser = if (user.id == null) {
             repository.save(LuckUserRechargeWalletPO().apply {
                this.userId=userId
                this.groupId=groupId
                this.type= UserRechargeWalletType.TRC20.code
            })
        } else {
            user
        }
        return LuckUserRechargeWalletMapper.MAPPER.po2vo(actualUser)
    }

}
