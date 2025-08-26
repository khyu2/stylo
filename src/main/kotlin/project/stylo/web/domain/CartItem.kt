package project.stylo.web.domain

import java.time.LocalDateTime

data class CartItem(
    val cartItemId: Long,
    val memberId: Long,
    val productId: Long? = null,
    val optionId: Long? = null,
    val quantity: Long? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)