package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOrders
import org.springframework.stereotype.Repository
import project.stylo.web.domain.Orders

@Repository
class OrdersDao(private val dsl: DSLContext) {
    companion object {
        private val ORDERS = JOrders.ORDERS
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

    fun findAllByMemberId(memberId: Long): List<Orders> =
        dsl.selectFrom(ORDERS)
            .where(ORDERS.MEMBER_ID.eq(memberId))
            .and(ORDERS.DELETED_AT.isNull)
            .orderBy(ORDERS.ORDER_ID.desc())
            .fetchInto(Orders::class.java)
}