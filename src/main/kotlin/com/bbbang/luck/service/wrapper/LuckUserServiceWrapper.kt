package com.bbbang.luck.service.wrapper

import com.bbbang.luck.api.bot.type.LuckUserType
import com.bbbang.luck.api.bot.type.BotMessageType
import com.bbbang.luck.domain.bo.LuckUserBO
import com.bbbang.luck.domain.po.LuckUserPO
import com.bbbang.luck.domain.type.LuckUserRoleType
import com.bbbang.luck.domain.vo.LuckUserVO
import com.bbbang.luck.helper.SassIdHelper
import com.bbbang.luck.repository.LuckUserRepository
import com.bbbang.luck.service.LuckUserService
import com.bbbang.parent.exception.BusinessException
import io.micronaut.cache.annotation.CachePut
import io.micronaut.cache.annotation.Cacheable
import io.micronaut.chatbots.telegram.api.CallbackQuery
import io.micronaut.chatbots.telegram.api.Chat
import io.micronaut.chatbots.telegram.api.Update
import io.micronaut.chatbots.telegram.api.User
import jakarta.inject.Singleton

import reactor.core.publisher.Mono


@Singleton
open class LuckUserServiceWrapper(private val luckUserService: LuckUserService,
                                  private val luckUserRepository: LuckUserRepository) {

    fun findByBotUser(callbackQuery: CallbackQuery): LuckUserVO? {
        return this.findByBotUser(callbackQuery.from, callbackQuery.message.chat?.id, callbackQuery.message.chat?.type)
    }


    fun findByBotUser(update: Update): LuckUserVO? {
        val botUser = update.message.from
        val chat = update.message.chat
        return this.findByBotUser(botUser, chat)
    }

    fun findByBotUser(botUser: User?, chat: Chat?): LuckUserVO? {
        return this.findByBotUser(botUser, chat?.id, chat?.type)
    }


    fun findByBotUser(botUser: User?, groupId: Long?, chatType: String?): LuckUserVO? {
        val sassId = SassIdHelper.getSassId(groupId, botUser)
        val result = this.findByBotUser(botUser, sassId, groupId, chatType)
        return result//.map { println("sassId=$sassId  ${it.userName}" ); it }
    }


    @CachePut("#sassId")
    open fun updateRolesById(userId: Long?, sassId: String?, roles: String?): Int? {
        if (sassId != null) {
            return luckUserRepository.updateRolesById(userId, roles)
        }
        return null
    }

    /**
     * 这里更新邀请人信息，一般不影响查询
     */
    @CachePut("#sassId")
    open fun updateInviterUserIdById(userId: Long?, sassId: String?, inviterUserId: Long?): Int? {
        if (sassId == null) {
            return null
        }
        return luckUserRepository.updateInviterUserIdById(userId, inviterUserId)
    }

    @Cacheable("#sassId")
    open fun findByBotUser(botUser: User?, sassId: String, groupId: Long?, chatType: String?): LuckUserVO? {
        val user = luckUserService.findByBotUserId(botUser?.id!!, groupId)
        val luckUser = if (user.id == null) {
            luckUserService.save(LuckUserBO().apply {
                this.botUserId = botUser.id
                this.groupId = groupId
                this.firstName = botUser.firstName
                this.lastName = botUser.lastName
                this.userName = botUser.username
                this.status = LuckUserType.ENABLE.code
                this.roles = LuckUserRoleType.USER.code
            })
        } else {
            user
        }
        if (luckUser.status == LuckUserType.DISABLE.code) {
            throw BusinessException("用户[${user.firstName}]已经被禁用")
        }
        return if (user.groupId == null && BotMessageType.SUPERGROUP.code == chatType) {
            luckUserService.update(user.id!!, LuckUserBO().apply {
                this.groupId = groupId
            })
        } else {
            user
        }
}






}