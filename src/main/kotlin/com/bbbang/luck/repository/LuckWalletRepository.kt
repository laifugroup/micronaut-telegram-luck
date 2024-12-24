package com.bbbang.luck.repository;
import com.bbbang.luck.domain.po.LuckWalletPO
import com.bbbang.parent.repository2.BasePageableRepository2
import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.annotation.RepositoryConfiguration
import io.micronaut.data.model.query.builder.jpa.JpaQueryBuilder
import java.math.BigDecimal

@Repository
@RepositoryConfiguration(queryBuilder = JpaQueryBuilder::class)
interface LuckWalletRepository: BasePageableRepository2<LuckWalletPO>   {


    fun findByUserId(@NonNull userId: Long?): LuckWalletPO?

    fun findByUserIdInList(@NonNull userIdList: List<Long?>): List<LuckWalletPO>

    @Query("UPDATE LuckWalletPO t SET t.credit = :credit where t.id = :id")
    fun updateCreditById(credit: BigDecimal?,id:Long?):Int

//UPDATE LuckWalletPO t SET credit = :credit where userId = :userId
//    session.createQuery<Long>("UPDATE LuckWalletPO t SET credit = :credit where id = :id")
//    .setParameter("credit", balance)
//    .setParameter("id", wallet.id)
//    .executeUpdate()
}

