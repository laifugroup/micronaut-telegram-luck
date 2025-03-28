package com.bbbang.luck.service;

import com.bbbang.luck.repository.LuckWalletRepository
import com.bbbang.luck.domain.vo.LuckWalletVO
import com.bbbang.luck.domain.bo.LuckWalletBO
import com.bbbang.luck.domain.dto.LuckWalletDTO
import com.bbbang.luck.domain.dto.LuckWalletPageDTO
import com.bbbang.luck.domain.po.LuckWalletPO
import com.bbbang.luck.domain.type.WalletStatus
import com.bbbang.luck.mapper.LuckWalletMapper

import com.bbbang.parent.service2.impl.BaseServiceImpl2
import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import reactor.core.publisher.Mono
import java.math.BigDecimal


@Singleton
open class LuckWalletService(private val repository: LuckWalletRepository) 
:BaseServiceImpl2<LuckWalletDTO, LuckWalletPageDTO, LuckWalletBO, LuckWalletPO, LuckWalletVO>(repository, LuckWalletMapper.MAPPER){


    fun findWalletByUserId(userId:Long?,groupId:Long?) : LuckWalletVO {
     val wallet= repository.findByUserId(userId)
        val myWallet=if (wallet?.id==null){
            repository .save(LuckWalletPO().apply {
                this.userId=userId
                this.credit= BigDecimal.ZERO
                this.status= WalletStatus.ENABLE.code
                this.groupId=groupId
            })
        }else{
            wallet
        }
       return LuckWalletMapper.MAPPER.po2vo(myWallet)
    }

    fun findByUserIdInList(@NonNull userIdList: List<Long?>): List<LuckWalletPO>{
        return repository.findByUserIdInList(userIdList)
    }

    /**
     * 更新值 不是更新以后的值
     */
    fun updateCreditById(addCredit: BigDecimal?, id:Long?):Int{
        return repository.updateCreditById(addCredit,id)
    }

}
