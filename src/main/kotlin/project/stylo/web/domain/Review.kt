package project.stylo.web.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class Review(
    val reviewId: Long? = null,
    val memberId: Long,
    val orderItemId: Long,
    val productId: Long,
    val rating: BigDecimal,
    val title: String,
    val content: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
)
