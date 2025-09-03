package project.stylo.web.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderItem(
    val orderItemId: Long,
    val orderId: Long,
    val productOptionId: Long,
    val quantity: Int,
    val price: BigDecimal,
    val createdAt: LocalDateTime,
)
