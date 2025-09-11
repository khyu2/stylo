package project.stylo.web.domain

import java.time.LocalDateTime

data class Wishlist(
    val wishlistId: Long,
    val memberId: Long,
    val productId: Long,
    val createdAt: LocalDateTime
)
