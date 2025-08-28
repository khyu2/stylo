package project.stylo.web.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductOption(
    val productOptionId: Long,
    val productId: Long,
    val sku: String,
    val additionalPrice: BigDecimal,
    val stock: Long,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
)
