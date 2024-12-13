package com.bbbang.luck.repository;
import com.bbbang.luck.domain.po.LuckInvitePO
import com.bbbang.parent.repository.BaseReactorPageableRepository
import com.bbbang.parent.repository2.BasePageableRepository2
import io.micronaut.data.annotation.Repository
import io.micronaut.data.annotation.RepositoryConfiguration
import io.micronaut.data.model.query.builder.jpa.JpaQueryBuilder
import reactor.core.publisher.Mono

@Repository
@RepositoryConfiguration(queryBuilder = JpaQueryBuilder::class)
interface LuckInviteRepository: BasePageableRepository2<LuckInvitePO>   {

   fun findOneByUserIdOrderByIdDesc(userId: Long): LuckInvitePO?
   fun findByUrlHash(urlHash: String): LuckInvitePO?


}

