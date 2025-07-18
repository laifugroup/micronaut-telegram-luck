package com.bbbang.luck.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import io.micronaut.core.annotation.Introspected
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import jakarta.validation.constraints.*
import org.hibernate.annotations.Comment
import java.math.BigDecimal

@Schema()
@Introspected
data class LuckSendLuckDTO(

    @field:Schema(description = "用户ID[1541693333435453411]",type="string",example = "1339468674200637453",required=true)
    @field:JsonSerialize(using = ToStringSerializer::class)
    var  userId:Long?=null,


    @field:Schema(description = "消息ID[123]",type="string",example = "1339468674200637453")
    @field:JsonSerialize(using = ToStringSerializer::class)
    var  messageId:Long?=null,

    @field:Schema(description = "积分[100]",example = "1",required=true)
    var  credit:BigDecimal?=null,

    @field:Schema(description = "倍率[1.8]",example = "1.8")
    var  odds:BigDecimal?=null,


    @field:Schema(description = "红包个数[6]",example = "1",required=true)
    var  redPackNumbers:Int?=null,
	

    @field:Schema(description = "中雷数字[1]",example = "1",required=true)
    var  boomNumber:Int?=null,
	

    @field:Schema(description = "博弈组ID[112333222]",type="string",example = "1339468674200637453",required=true)
    @field:JsonSerialize(using = ToStringSerializer::class)
    var  groupId:Long?=null,
	

    @field:Schema(description = "名字[san]",example = "san",required=true)
	@field:NotBlank(message = "[名字]不能为空")
	@field:Pattern(regexp = "\\S*",message="[名字]不符合规则")
    @field:Size(min=2, max = 64, message = "[名字]长度范围2-64")
    var  firstName:String?=null,
	

    @field:Schema(description = "姓[zhang]",example = "zhang",required=true)
	@field:NotBlank(message = "[姓]不能为空")
	@field:Pattern(regexp = "\\S*",message="[姓]不符合规则")
    @field:Size(min=2, max = 64, message = "[姓]长度范围2-64")
    var  lastName:String?=null,
	

    @field:Schema(description = "用户名[laowang]",example = "laowang",required=true)
	@field:NotBlank(message = "[用户名]不能为空")
	@field:Pattern(regexp = "\\S*",message="[用户名]不符合规则")
    @field:Size(min=2, max = 64, message = "[用户名]长度范围2-64")
    var  userName:String?=null,
	

    @field:Schema(description = "状态[1-已结算,2-未结算,3-已过期,4-已撤销]",example = "1",required=true)
    var  status:Int?=null,
	
) {
 
}
