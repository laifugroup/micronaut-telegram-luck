package com.bbbang.luck.repository;
import com.bbbang.luck.domain.po.LuckWalletPO
import com.bbbang.parent.repository.BaseReactorPageableRepository
import com.bbbang.parent.repository2.BasePageableRepository2
import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Repository
import io.micronaut.data.annotation.RepositoryConfiguration
import io.micronaut.data.model.query.builder.jpa.JpaQueryBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
@RepositoryConfiguration(queryBuilder = JpaQueryBuilder::class)
interface LuckWalletRepository: BasePageableRepository2<LuckWalletPO>   {


    fun findByUserId(@NonNull userId: Long): LuckWalletPO

    fun findByUserIdInList(@NonNull userIdList: List<Long?>): List<LuckWalletPO>

}

