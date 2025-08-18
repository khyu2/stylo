package project.stylo.web.domain

import java.time.LocalDateTime

data class Review(
    val reviewId: Long,
    val memberId: Long,
    val productId: Long,
    val rating: Int? = null,
    val content: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
)