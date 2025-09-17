package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOrderItem
import org.jooq.generated.tables.JOrders
import org.jooq.generated.tables.JPayment
import org.jooq.generated.tables.JProduct
import org.jooq.generated.tables.JProductOption
import org.jooq.generated.tables.JReview
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import project.stylo.common.utils.JooqUtils.Companion.andIfNotNull
import project.stylo.common.utils.JooqUtils.Companion.likeIgnoreCaseIfNotBlank
import project.stylo.web.domain.Orders
import project.stylo.web.domain.enums.OrderStatus
import project.stylo.web.domain.enums.PaymentStatus
import project.stylo.web.dto.request.OrdersSearchRequest
import project.stylo.web.dto.response.OrderItemResponse
import project.stylo.web.dto.response.OrderResponse
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class OrdersDao(private val dsl: DSLContext) {
    companion object {
        private val REVIEW = JReview.REVIEW
        private val ORDERS = JOrders.ORDERS
        private val ORDER_ITEM = JOrderItem.ORDER_ITEM
        private val PAYMENT = JPayment.PAYMENT
        private val PRODUCT = JProduct.PRODUCT
        private val PRODUCT_OPTION = JProductOption.PRODUCT_OPTION
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

    // 결제 상태가 없는 주문 정보도 포함해서 가져옴 : left join(Payment)
    fun findAllByMemberId(memberId: Long, pageable: Pageable): Page<OrderResponse> {
        val baseQuery = dsl.select(
            ORDERS.ORDER_ID.`as`("orderId"),
            ORDERS.TOTAL_AMOUNT.`as`("totalAmount"),
            ORDERS.STATUS.`as`("status"),
            PAYMENT.ORDER_UID.`as`("orderUid"),
            PAYMENT.PAYMENT_KEY.`as`("paymentKey"),
            PAYMENT.STATUS.`as`("paymentStatus"),
            PRODUCT.PRODUCT_ID.`as`("productId"),
            PRODUCT.NAME.`as`("name"),
            PRODUCT.THUMBNAIL_URL.`as`("thumbnailUrl"),
            PRODUCT_OPTION.SKU.`as`("optionSku"),
            ORDER_ITEM.QUANTITY.`as`("quantity"),
            ORDER_ITEM.PRICE.`as`("unitPrice"),
            ORDER_ITEM.PRICE.multiply(ORDER_ITEM.QUANTITY).`as`("totalPrice"),
            DSL.exists(
                DSL.selectFrom(REVIEW)
                    .where(
                        REVIEW.ORDER_ITEM_ID.eq(ORDER_ITEM.ORDER_ITEM_ID)
                            .and(REVIEW.DELETED_AT.isNull)
                    )
            ).`as`("hasReview"),
            ORDERS.CREATED_AT.`as`("createdAt"),
        )
            .from(ORDERS)
            .leftJoin(PAYMENT).on(PAYMENT.ORDER_ID.eq(ORDERS.ORDER_ID))
            .join(ORDER_ITEM).on(ORDER_ITEM.ORDER_ID.eq(ORDERS.ORDER_ID))
            .join(PRODUCT_OPTION).on(PRODUCT_OPTION.PRODUCT_OPTION_ID.eq(ORDER_ITEM.PRODUCT_OPTION_ID))
            .join(PRODUCT).on(PRODUCT.PRODUCT_ID.eq(PRODUCT_OPTION.PRODUCT_ID))
            .where(ORDERS.MEMBER_ID.eq(memberId))
            .and(ORDERS.DELETED_AT.isNull)

        val total = dsl.select(DSL.countDistinct(ORDERS.ORDER_ID))
            .from(ORDERS)
            .join(ORDER_ITEM).on(ORDER_ITEM.ORDER_ID.eq(ORDERS.ORDER_ID))
            .where(ORDERS.MEMBER_ID.eq(memberId))
            .and(ORDERS.DELETED_AT.isNull)
            .fetchOne(0, Int::class.java)!!

        val records = baseQuery.orderBy(ORDERS.CREATED_AT.desc(), ORDERS.ORDER_ID.asc())
            .limit(pageable.pageSize * 10) // 조인으로 인한 레코드 증가 고려
            .offset(pageable.offset)
            .fetch()

        val content = records.groupBy { it.getValue("orderId") as Long }
            .map { (orderId, orderRecords) ->
                val firstRecord = orderRecords.first()
                OrderResponse(
                    orderId = orderId,
                    totalAmount = firstRecord.getValue("totalAmount") as BigDecimal,
                    status = OrderStatus.valueOf(firstRecord.getValue("status") as String),
                    orderUid = firstRecord.getValue("orderUid") as String?,
                    paymentKey = firstRecord.getValue("paymentKey") as String?,
                    paymentStatus = (firstRecord.getValue("paymentStatus") as String?)?.let { PaymentStatus.valueOf(it) },
                    orderItems = orderRecords.map { record ->
                        OrderItemResponse(
                            productId = record.getValue("productId") as Long,
                            name = record.getValue("name") as String,
                            thumbnailUrl = record.getValue("thumbnailUrl") as String,
                            optionSku = record.getValue("optionSku") as String,
                            quantity = record.getValue("quantity") as Long,
                            unitPrice = record.getValue("unitPrice") as BigDecimal,
                            totalPrice = record.getValue("totalPrice") as BigDecimal,
                            hasReview = record.getValue("hasReview") as Boolean
                        )
                    },
                    createdAt = firstRecord.getValue("createdAt") as LocalDateTime?
                )
            }
            .take(pageable.pageSize)

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