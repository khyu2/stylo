package project.stylo.web.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class Product(
    val productId: Long,
    val categoryId: Long,
    val name: String,
    val description: String? = null,
    val price: BigDecimal,
    val thumbnailUrl: String? = null,
    val createdBy: Long,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
)