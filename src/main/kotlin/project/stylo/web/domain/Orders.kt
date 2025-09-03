package project.stylo.web.domain

import project.stylo.web.domain.enums.OrderStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class Orders(
    val orderId: Long? = null,
    val memberId: Long,
    val addressId: Long,
    val totalAmount: BigDecimal,
    val status: OrderStatus,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null,
)