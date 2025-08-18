package project.stylo.web.domain

import project.stylo.web.domain.enums.OrderStatus
import java.time.LocalDateTime

data class Orders(
    val orderId: Long,
    val memberId: Long,
    val addressId: Long,
    val status: OrderStatus,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)