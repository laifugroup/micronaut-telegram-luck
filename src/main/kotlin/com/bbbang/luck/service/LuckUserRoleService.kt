package com.bbbang.luck.service

import com.bbbang.luck.domain.type.LuckUserRoleType
import com.bbbang.luck.domain.vo.LuckRoleVO
import jakarta.inject.Singleton
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Singleton
class LuckUserRoleService {

    fun getRoleList():List<LuckRoleVO>{
       val roles= listOf(LuckRoleVO(LuckUserRoleType.USER.code,LuckUserRoleType.USER.message)
            ,LuckRoleVO(LuckUserRoleType.ADMIN.code,LuckUserRoleType.ADMIN.message),
            LuckRoleVO(LuckUserRoleType.FINANCE.code,LuckUserRoleType.FINANCE.message))
      return  roles
    }
}