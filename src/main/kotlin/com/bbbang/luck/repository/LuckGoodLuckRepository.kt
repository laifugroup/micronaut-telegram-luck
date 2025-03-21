package com.bbbang.luck.repository;
import com.bbbang.luck.domain.po.LuckGoodLuckPO

import com.bbbang.parent.repository2.BasePageableRepository2
import io.micronaut.data.annotation.Repository
import io.micronaut.data.annotation.RepositoryConfiguration
import io.micronaut.data.model.query.builder.jpa.JpaQueryBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
@RepositoryConfiguration(queryBuilder = JpaQueryBuilder::class)
interface LuckGoodLuckRepository: BasePageableRepository2<LuckGoodLuckPO>   {

   fun existsByLuckRedPackIdAndUserId(luckRedPackId: Long?, userId: Long?): Boolean

   fun countByLuckRedPackId(luckRedPackId: Long?): Int

   fun findByLuckRedPackId(luckRedPackId: Long?): List<LuckGoodLuckPO>


   //UPDATE LuckGoodLuckPO t SET credit = :credit,lastNumber = :lastNumber where id = :id
   fun   updateCreditAndLastNumberById(credit:Long?,lastNumber:Int?,id:Long?):Int

}

