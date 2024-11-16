package com.bbbang.luck.repository;
import com.bbbang.luck.domain.po.LuckChargeLogPO
import com.bbbang.parent.repository.BaseReactorPageableRepository
import com.bbbang.parent.repository2.BasePageableRepository2
import io.micronaut.data.annotation.Repository
import io.micronaut.data.annotation.RepositoryConfiguration
import io.micronaut.data.model.query.builder.jpa.JpaQueryBuilder

@Repository
@RepositoryConfiguration(queryBuilder = JpaQueryBuilder::class)
interface LuckChargeLogRepository: BasePageableRepository2<LuckChargeLogPO>   {

   
}

