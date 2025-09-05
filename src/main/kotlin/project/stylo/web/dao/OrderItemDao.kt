package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOrderItem
import org.springframework.stereotype.Repository
import project.stylo.web.domain.OrderItem

@Repository
class OrderItemDao(private val dsl: DSLContext) {
    companion object {
        private val ORDER_ITEM = JOrderItem.ORDER_ITEM
    }

    fun save(orderItem: OrderItem): Long =
        dsl.insertInto(ORDER_ITEM)
            .set(ORDER_ITEM.ORDER_ID, orderItem.orderId)
            .set(ORDER_ITEM.PRODUCT_OPTION_ID, orderItem.productOptionId)
            .set(ORDER_ITEM.QUANTITY, orderItem.quantity)
            .set(ORDER_ITEM.PRICE, orderItem.price)
            .returning(ORDER_ITEM.ORDER_ITEM_ID)
            .fetchOne(ORDER_ITEM.ORDER_ITEM_ID)!!

    fun saveAll(orderId: Long, items: List<OrderItem>): Int {
        if (items.isEmpty()) return 0
        var insert = dsl.insertInto(
            ORDER_ITEM,
            ORDER_ITEM.ORDER_ID,
            ORDER_ITEM.PRODUCT_OPTION_ID,
            ORDER_ITEM.QUANTITY,
            ORDER_ITEM.PRICE
        )
        items.forEach { it ->
            insert = insert.values(orderId, it.productOptionId, it.quantity, it.price)
        }
        return insert.execute()
    }

    fun findByOrderId(orderId: Long): List<OrderItem> =
        dsl.selectFrom(ORDER_ITEM)
            .where(ORDER_ITEM.ORDER_ID.eq(orderId))
            .fetchInto(OrderItem::class.java)
}