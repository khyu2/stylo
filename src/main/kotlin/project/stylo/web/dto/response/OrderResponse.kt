package project.stylo.web.dto.response

import project.stylo.web.domain.Orders
import project.stylo.web.domain.Payment
import project.stylo.web.domain.enums.OrderStatus
import project.stylo.web.domain.enums.PaymentStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderResponse(
    val orderId: Long,
    val totalAmount: BigDecimal,
    val status: OrderStatus,
    val orderUid: String?,
    val paymentKey: String?,
    val paymentStatus: PaymentStatus?,
    val orderItems: List<OrderItemResponse>? = null,
    val createdAt: LocalDateTime?,
) {
    companion object {
        fun from(orders: Orders, payment: Payment?): OrderResponse {
            return OrderResponse(
                orderId = orders.orderId!!,
                totalAmount = orders.totalAmount,
                status = orders.status,
                orderUid = payment?.orderUid,
                paymentKey = payment?.paymentKey,
                paymentStatus = payment?.status,
                createdAt = orders.createdAt,
            )
        }
    }
}
