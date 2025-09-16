package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JPayment
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import project.stylo.web.domain.Payment
import project.stylo.web.domain.enums.PaymentStatus
import java.math.BigDecimal
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class PaymentDao(private val dsl: DSLContext) {
    companion object {
        private val PAYMENT = JPayment.PAYMENT
    }

    fun save(payment: Payment) =
        dsl.insertInto(PAYMENT)
            .set(PAYMENT.ORDER_ID, payment.orderId)
            .set(PAYMENT.MEMBER_ID, payment.memberId)
            .set(PAYMENT.ORDER_UID, payment.orderUid)
            .set(PAYMENT.PAYMENT_KEY, payment.paymentKey)
            .set(PAYMENT.AMOUNT, payment.amount)
            .set(PAYMENT.CURRENCY, payment.currency)
            .set(PAYMENT.METHOD, payment.method.name)
            .set(PAYMENT.PG_PROVIDER, payment.pgProvider.name)
            .set(PAYMENT.TRANSACTION_ID, payment.transactionId)
            .set(PAYMENT.STATUS, payment.status.name)
            .set(PAYMENT.APPROVED_AT, payment.approvedAt)
            .set(PAYMENT.CREATED_AT, payment.createdAt)
            .set(PAYMENT.UPDATED_AT, payment.updatedAt)
            .execute()

    /**
     * 결제 완료 처리
     * @return orderId
     */
    fun confirm(orderUid: String, paymentKey: String, transactionId: String): Long =
        dsl.update(PAYMENT)
            .set(PAYMENT.PAYMENT_KEY, paymentKey)
            .set(PAYMENT.TRANSACTION_ID, transactionId)
            .set(PAYMENT.STATUS, PaymentStatus.DONE.name)
            .set(PAYMENT.APPROVED_AT, LocalDateTime.now())
            .set(PAYMENT.UPDATED_AT, LocalDateTime.now())
            .where(PAYMENT.PAYMENT_KEY.isNull)
            .and(PAYMENT.ORDER_UID.eq(orderUid))
            .returning(PAYMENT.ORDER_ID)
            .fetchOne(PAYMENT.ORDER_ID)!!

    fun findByPaymentKey(paymentKey: String): Payment? =
        dsl.selectFrom(PAYMENT)
            .where(PAYMENT.PAYMENT_KEY.eq(paymentKey))
            .fetchOneInto(Payment::class.java)

    fun findByOrderId(orderId: Long): Payment? =
        dsl.selectFrom(PAYMENT)
            .where(PAYMENT.ORDER_ID.eq(orderId))
            .fetchOneInto(Payment::class.java)

    fun findByOrderIds(orderIds: Collection<Long>): Map<Long, Payment> =
        if (orderIds.isEmpty()) emptyMap() else
            dsl.selectFrom(PAYMENT)
                .where(PAYMENT.ORDER_ID.`in`(orderIds))
                .fetchInto(Payment::class.java)
                .associateBy { it.orderId }

    fun totalSalesDone(): BigDecimal =
        dsl.select(DSL.coalesce(DSL.sum(PAYMENT.AMOUNT), BigDecimal.ZERO))
            .from(PAYMENT)
            .where(PAYMENT.STATUS.eq(PaymentStatus.DONE.name))
            .fetchOne(0, BigDecimal::class.java) ?: BigDecimal.ZERO

    fun monthlySalesDone(start: LocalDateTime, end: LocalDateTime): BigDecimal =
        dsl.select(DSL.coalesce(DSL.sum(PAYMENT.AMOUNT), BigDecimal.ZERO))
            .from(PAYMENT)
            .where(PAYMENT.STATUS.eq(PaymentStatus.DONE.name))
            .and(PAYMENT.APPROVED_AT.ge(start))
            .and(PAYMENT.APPROVED_AT.lt(end))
            .fetchOne(0, BigDecimal::class.java) ?: BigDecimal.ZERO

    fun salesByDaySince(since: LocalDate): Map<LocalDate, BigDecimal> {
        val records = dsl.select(
            DSL.cast(PAYMENT.APPROVED_AT.cast(Date::class.java), Date::class.java),
            DSL.coalesce(DSL.sum(PAYMENT.AMOUNT), BigDecimal.ZERO)
        )
            .from(PAYMENT)
            .where(PAYMENT.STATUS.eq(PaymentStatus.DONE.name))
            .and(PAYMENT.APPROVED_AT.ge(since.atStartOfDay()))
            .groupBy(DSL.cast(PAYMENT.APPROVED_AT.cast(Date::class.java), Date::class.java))
            .fetch()
        return records.associate { r ->
            val date = r.get(0, Date::class.java).toLocalDate()
            val amt = r.get(1, BigDecimal::class.java) ?: BigDecimal.ZERO
            date to amt
        }
    }
}