//package com.bbbang.luck.service;
//
//import com.bbbang.luck.api.bot.type.CreditLogType
//import com.bbbang.luck.repository.LuckCreditApplyRepository
//import com.bbbang.luck.service.LuckCreditApplyService
//import com.bbbang.luck.domain.vo.LuckCreditApplyVO
//import com.bbbang.luck.domain.bo.LuckCreditApplyBO
//import com.bbbang.luck.domain.dto.LuckCreditApplyAuditDTO
//import com.bbbang.luck.domain.dto.LuckCreditApplyDTO
//import com.bbbang.luck.domain.dto.LuckCreditApplyPageDTO
//import com.bbbang.luck.domain.po.LuckCreditApplyPO
//import com.bbbang.luck.domain.po.LuckCreditLogPO
//import com.bbbang.luck.domain.type.UpDownCreditStatus
//import com.bbbang.luck.domain.type.UpDownCreditType
//import com.bbbang.luck.mapper.LuckCreditApplyMapper
//import com.bbbang.parent.exception.BusinessException
//
//import com.bbbang.parent.service2.impl.BaseServiceImpl2
//import com.bbbang.parent.utils.BigDecimalUtils
//import jakarta.inject.Inject
//import jakarta.inject.Singleton
//import jakarta.transaction.Transactional
//import org.hibernate.SessionFactory
//import reactor.core.publisher.Mono
//import java.math.BigDecimal
//
//
//@Singleton
//open class LuckCreditApplyService(private val repository: LuckCreditApplyRepository,
//                                  private val luckWalletService: LuckWalletService,
//
//                                )
//:BaseServiceImpl2<LuckCreditApplyDTO, LuckCreditApplyPageDTO, LuckCreditApplyBO, LuckCreditApplyPO, LuckCreditApplyVO>(repository, LuckCreditApplyMapper.MAPPER){
//
//
//
//    fun audit(auditDTO: LuckCreditApplyAuditDTO):Unit{
//        if (auditDTO.status != UpDownCreditStatus.PASS.code && auditDTO.status != UpDownCreditStatus.REJECT.code){
//          throw  BusinessException("[错误]处理状态只能是通过|拒绝。")
//        }
//       val luckCreditApplyOptional= repository.findById(auditDTO.id)
//        if (luckCreditApplyOptional.isEmpty){
//            throw  BusinessException("[错误]数据不存在。")
//        }
//        val luckCreditApply=luckCreditApplyOptional.get()
//        if (luckCreditApply.status!=UpDownCreditStatus.APPLY.code){
//            throw BusinessException("[错误]本申请已处理,不能重复处理。")
//        }else  if (luckCreditApply.type==UpDownCreditType.UP.code && auditDTO.credit?.compareTo(BigDecimal.ZERO)==BigDecimalUtils.SMALL){
//            throw BusinessException("[错误]上分金额必须为正数。")
//        }else if (luckCreditApply.type==UpDownCreditType.DOWN.code && auditDTO.credit?.compareTo(BigDecimal.ZERO)==BigDecimalUtils.BIG){
//            throw BusinessException("[错误]下分金额必须为负数。")
//        }
//       val luckCreditApplyNew= repository.update(luckCreditApply.apply {
//            this.credit=auditDTO.credit
//            this.fromAddress=auditDTO.fromAddress
//            this.toAddress=auditDTO.toAddress
//            this.txnHash=auditDTO.txnHash
//            this.status=auditDTO.status
//            this.remark=auditDTO.remark ?: UpDownCreditStatus.entries.first { it.code==auditDTO.status }.message
//        })
//
//        auditTransaction(luckCreditApplyNew)
//    }
//
//    @Transactional
//    fun auditTransaction(luckCreditApplyNew:  LuckCreditApplyPO):Unit{
//        //     上分     下分
//        //同意  ++       empty
//        //拒绝  empty    ++
//        if ((luckCreditApplyNew.type==UpDownCreditType.UP.code && luckCreditApplyNew.status== UpDownCreditStatus.PASS.code)
//            ||
//            luckCreditApplyNew.type==UpDownCreditType.DOWN.code && luckCreditApplyNew.status== UpDownCreditStatus.REJECT.code
//        ){
//            val userWallet= luckWalletService.findWalletByUserId(luckCreditApplyNew.userId,luckCreditApplyNew.groupId)
//
//            //上分 +分
//            if (luckCreditApplyNew.type==UpDownCreditType.UP.code && luckCreditApplyNew.status== UpDownCreditStatus.PASS.code){
//                //更新钱包和明细
//                return session.createQuery<Long>("UPDATE LuckWalletPO t SET credit = credit + :credit where userId = :userId")
//                    .setParameter("credit", luckCreditApplyNew.credit)
//                    .setParameter("userId", luckCreditApplyNew.userId)
//                    .executeUpdate()
//
//                val luckCreditLogPO= LuckCreditLogPO().apply {
//                    this.credit = luckCreditApply.credit
//                    this.userId = luckCreditApply.userId
//                    this.type = CreditLogType.CREDIT_UP_PASS.code
//                    this.remark = "${CreditLogType.CREDIT_UP_PASS.desc}[${luckCreditApply.id}]"
//                    this.creditBefore = userWallet.credit
//                    this.creditAfter = this.creditBefore?.plus(this.credit?: BigDecimal.ZERO)
//                    this.groupId=userWallet.groupId
//                }
//                session.persist(luckCreditLogPO).thenApply {
//                    Unit
//                }
//
//            } else  if ( luckCreditApplyNew.type==UpDownCreditType.DOWN.code && luckCreditApplyNew.status== UpDownCreditStatus.REJECT.code){
//                //下分 拒绝 +分(因为在提交的时候，就预扣了)
//                return@flatMap Mono.fromCompletionStage(sessionFactoryStage.withTransaction { session ->
//                    session.createQuery<Long>("UPDATE LuckWalletPO t SET credit = credit + :credit where userId = :userId")
//                        .setParameter("credit", luckCreditApplyNew.credit?.abs())
//                        .setParameter("userId", luckCreditApplyNew.userId)
//                        .executeUpdate()
//
//                    val luckCreditLogPO= LuckCreditLogPO().apply {
//                        this.credit = luckCreditApplyNew.credit?.abs()
//                        this.userId = luckCreditApplyNew.userId
//                        this.type = CreditLogType.CREDIT_DOWN_REJECT.code
//                        this.remark = "${CreditLogType.CREDIT_DOWN_REJECT.desc}[${luckCreditApplyNew.id}]"
//                        this.creditBefore = userWallet.credit
//                        this.creditAfter = this.creditBefore?.plus(this.credit?: BigDecimal.ZERO)
//                        this.groupId=userWallet.groupId
//                    }
//                    session.persist(luckCreditLogPO).thenApply {
//                        Unit
//                    }
//                })
//            }
//        }
//
//    }
//
//}
