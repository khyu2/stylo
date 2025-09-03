package project.stylo.web.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderItem(
    val orderItemId: Long? = null,
    val orderId: Long?,
    val productOptionId: Long,
    val quantity: Long,
    val price: BigDecimal,
    val createdAt: LocalDateTime? = null,
)
