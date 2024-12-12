package com.bbbang.luck.service.wrapper

import com.bbbang.luck.api.bot.type.LuckUserType
import com.bbbang.luck.api.bot.type.BotMessageType
import com.bbbang.luck.domain.bo.LuckPlatformBO
import com.bbbang.luck.domain.bo.LuckUserBO
import com.bbbang.luck.domain.type.LuckUserRoleType
import com.bbbang.luck.domain.type.PlatformStatus
import com.bbbang.luck.domain.vo.LuckPlatformVO
import com.bbbang.luck.domain.vo.LuckUserVO
import com.bbbang.luck.service.LuckPlatformService
import com.bbbang.luck.service.LuckUserService
import com.bbbang.parent.exception.BusinessException
import io.micronaut.cache.annotation.CachePut
import io.micronaut.cache.annotation.Cacheable
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.User
import jakarta.inject.Singleton
import reactor.core.publisher.Mono


@Singleton
open class LuckPlatformServiceWrapper(private val luckPlatformService: LuckPlatformService) {

    fun findByGroupId(update: Update?): LuckPlatformVO? {
        return this.findByGroupId(update?.message?.chat?.id)
    }

    @Cacheable("#groupId")
    open fun findByGroupId(groupId: Long?): LuckPlatformVO? {
       val result= luckPlatformService.findByGroupId(groupId)
//         if (result?.id==null){
//             throw BusinessException("未查询到数据")
//         }
        return result
    }

    @CachePut("#groupId")
    open fun saveGroupId(botUser: User?,groupId: Long?,groupName:String?=null): LuckPlatformVO? {
        val platform= luckPlatformService.findByGroupId(groupId)
        val result=if(platform?.id==null){
            luckPlatformService.save(LuckPlatformBO(groupId=groupId,groupName = groupName, adminBotUserId = botUser?.id, status = PlatformStatus.ENABLE.code))
        }else{
            platform
        }
        return result
    }



    @CachePut("#groupId")
    open fun saveGroupId2(botUser: User?,groupId: Long?,groupName:String?=null): LuckPlatformVO? {
        return   luckPlatformService.save(LuckPlatformBO(groupId=groupId,groupName = groupName, adminBotUserId = botUser?.id, status = PlatformStatus.ENABLE.code))
    }


}