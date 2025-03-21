package com.bbbang.luck.repository;
import com.bbbang.luck.domain.po.LuckSendLuckPO

import com.bbbang.parent.repository2.BasePageableRepository2
import io.micronaut.data.annotation.Repository
import io.micronaut.data.annotation.RepositoryConfiguration
import io.micronaut.data.model.query.builder.jpa.JpaQueryBuilder

@Repository
@RepositoryConfiguration(queryBuilder = JpaQueryBuilder::class)
interface LuckSendLuckRepository: BasePageableRepository2<LuckSendLuckPO>   {

    //UPDATE LuckSendLuckPO t SET status = :status where id = :id
    fun  updateStatusById(status: Int,id:Long):Int

    fun findByStatusEquals(status:Int):List<LuckSendLuckPO>

}

