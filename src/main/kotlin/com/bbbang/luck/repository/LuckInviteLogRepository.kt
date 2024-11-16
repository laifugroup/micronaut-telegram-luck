package com.bbbang.luck.repository;
import com.bbbang.luck.domain.po.LuckInviteLogPO
import com.bbbang.parent.repository.BaseReactorPageableRepository
import com.bbbang.parent.repository2.BasePageableRepository2
import io.micronaut.data.annotation.Repository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.data.annotation.RepositoryConfiguration
import io.micronaut.data.model.query.builder.jpa.JpaQueryBuilder
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
@RepositoryConfiguration(queryBuilder = JpaQueryBuilder::class)
interface LuckInviteLogRepository: BasePageableRepository2<LuckInviteLogPO>   {

    fun existsByInviteeUserId(inviteeUserId:Long?): Boolean

    fun countByUserIdAndCreatedAtBetween(userId: Long, startDate: LocalDateTime, endDate: LocalDateTime):Int


    fun countByUserId(userId: Long):Int


    fun findByUserId(userId: Long,pageable: Pageable): Page<LuckInviteLogPO>
}

