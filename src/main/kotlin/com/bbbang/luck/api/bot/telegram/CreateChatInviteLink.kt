package com.bbbang.luck.api.bot.telegram

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.chatbots.telegram.api.send.Send
import io.micronaut.core.annotation.NonNull
import jakarta.validation.constraints.NotNull


//  https://core.telegram.org/bots/api#createChatInviteLink



 class CreateChatInviteLink(
    @JsonProperty("chat_id")
    @NotNull
    @NonNull
    val chatId: Long?,
    val name: String?,
    @JsonProperty("expire_date")
    val expireDate: Int? = null,
    @JsonProperty("member_limit")
    val memberLimit: Int? = 1000,
    @JsonProperty("creates_join_request")
    val createsJoinRequest: Boolean? = null,
     method: String?=CREATE_CHAT_INVITE_LINK,
): Send(method) {
     companion object{
         const val CREATE_CHAT_INVITE_LINK = "createchatinvitelink"
     }

}
//chat_id	Integer or String	Yes	Unique identifier for the target chat or username of the target channel (in the format @channelusername)
//name	String	Optional	Invite link name; 0-32 characters
//expire_date	Integer	Optional	Point in time (Unix timestamp) when the link will expire
//member_limit	Integer	Optional	The maximum number of users that can be members of the chat simultaneously after joining the chat via this invite link; 1-99999
//creates_join_request	Boolean	Optional