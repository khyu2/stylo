package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOrders
import org.jooq.generated.tables.JPayment
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import project.stylo.common.utils.JooqUtils.Companion.andIfNotNull
import project.stylo.common.utils.JooqUtils.Companion.likeIgnoreCaseIfNotBlank
import project.stylo.web.domain.Orders
import project.stylo.web.domain.enums.OrderStatus
import project.stylo.web.dto.request.OrdersSearchRequest
import project.stylo.web.dto.response.OrderResponse
import java.time.LocalDateTime

@Repository
class OrdersDao(private val dsl: DSLContext) {
    companion object {
        private val ORDERS = JOrders.ORDERS
        private val PAYMENT = JPayment.PAYMENT
    }

    fun save(orders: Orders): Long =
        dsl.insertInto(ORDERS)
            .set(ORDERS.MEMBER_ID, orders.memberId)
            .set(ORDERS.ADDRESS_ID, orders.addressId)
            .set(ORDERS.TOTAL_AMOUNT, orders.totalAmount)
            .set(ORDERS.STATUS, orders.status.name)
            .returning(ORDERS.ORDER_ID)
            .fetchOne(ORDERS.ORDER_ID)!!

    fun findById(id: Long): Orders? =
        dsl.selectFrom(ORDERS)
            .where(ORDERS.ORDER_ID.eq(id))
            .and(ORDERS.DELETED_AT.isNull)
            .fetchOneInto(Orders::class.java)

    fun findAllByMemberId(memberId: Long, pageable: Pageable): Page<OrderResponse> {
        val baseQuery = dsl.select(
            ORDERS.ORDER_ID.`as`("orderId"),
            ORDERS.TOTAL_AMOUNT.`as`("totalAmount"),
            ORDERS.STATUS.`as`("status"),
            PAYMENT.ORDER_UID.`as`("orderUid"),
            PAYMENT.PAYMENT_KEY.`as`("paymentKey"),
            PAYMENT.STATUS.`as`("paymentStatus"),
            ORDERS.CREATED_AT.`as`("createdAt"),
        )
            .from(ORDERS)
            .leftJoin(PAYMENT).on(PAYMENT.ORDER_ID.eq(ORDERS.ORDER_ID))
            .where(ORDERS.MEMBER_ID.eq(memberId))
            .and(ORDERS.DELETED_AT.isNull)

        val total = dsl.fetchCount(baseQuery)

        val content = baseQuery.orderBy(ORDERS.CREATED_AT.desc())
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .fetchInto(OrderResponse::class.java)

        return PageImpl(content, pageable, total.toLong())
    }

    fun findAll(request: OrdersSearchRequest, pageable: Pageable): Page<OrderResponse> {
        val baseQuery = dsl.select(
            ORDERS.ORDER_ID.`as`("orderId"),
            ORDERS.TOTAL_AMOUNT.`as`("totalAmount"),
            ORDERS.STATUS.`as`("status"),
            PAYMENT.ORDER_UID.`as`("orderUid"),
            PAYMENT.PAYMENT_KEY.`as`("paymentKey"),
            PAYMENT.STATUS.`as`("paymentStatus"),
            ORDERS.CREATED_AT.`as`("createdAt"),
        )
            .from(ORDERS)
            .leftJoin(PAYMENT).on(PAYMENT.ORDER_ID.eq(ORDERS.ORDER_ID))
            .where(ORDERS.DELETED_AT.isNull)
            .and(request.keyword.likeIgnoreCaseIfNotBlank(PAYMENT.ORDER_UID))
            .and(request.startDate.andIfNotNull { ORDERS.CREATED_AT.ge(it) })
            .and(request.endDate.andIfNotNull { ORDERS.CREATED_AT.le(it) })
            .and(request.minPrice.andIfNotNull { ORDERS.TOTAL_AMOUNT.ge(it.toBigDecimal()) })
            .and(request.maxPrice.andIfNotNull { ORDERS.TOTAL_AMOUNT.le(it.toBigDecimal()) })
            .and(request.orderStatus.andIfNotNull { ORDERS.STATUS.eq(it.name) })
            .and(request.paymentStatus.andIfNotNull { PAYMENT.STATUS.eq(it.name) })

        val total = dsl.fetchCount(baseQuery)

        val content = baseQuery
            .orderBy(ORDERS.CREATED_AT.desc())
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .fetchInto(OrderResponse::class.java)

        return PageImpl(content, pageable, total.toLong())
    }

    fun countAll(): Int =
        dsl.fetchCount(
            dsl.selectFrom(ORDERS).where(ORDERS.DELETED_AT.isNull)
        )

    fun countCreatedBetween(start: LocalDateTime, end: LocalDateTime): Int =
        dsl.fetchCount(
            dsl.selectFrom(ORDERS)
                .where(ORDERS.DELETED_AT.isNull)
                .and(ORDERS.CREATED_AT.ge(start))
                .and(ORDERS.CREATED_AT.lt(end))
        )

    fun countByStatusBetween(status: OrderStatus, start: LocalDateTime, end: LocalDateTime): Int =
        dsl.fetchCount(
            dsl.selectFrom(ORDERS)
                .where(ORDERS.DELETED_AT.isNull)
                .and(ORDERS.STATUS.eq(status.name))
                .and(ORDERS.CREATED_AT.ge(start))
                .and(ORDERS.CREATED_AT.lt(end))
        )

    fun updateStatus(orderId: Long, status: OrderStatus) =
        dsl.update(ORDERS)
            .set(ORDERS.STATUS, status.name)
            .set(ORDERS.UPDATED_AT, LocalDateTime.now())
            .where(ORDERS.ORDER_ID.eq(orderId))
            .and(ORDERS.DELETED_AT.isNull)
            .execute()
}