package project.stylo.web.domain

import java.time.LocalDateTime

data class CartItem(
    val cartItemId: Long? = null,
    val memberId: Long,
    val productOptionId: Long,
    val quantity: Long,
    val createdAt: LocalDateTime? = null,
)
