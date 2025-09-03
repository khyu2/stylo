package project.stylo.web.domain

import project.stylo.common.exception.BaseException
import project.stylo.web.exception.ProductExceptionType
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
) {
    fun validateStock(quantity: Long) {
        if (stock < quantity) throw BaseException(ProductExceptionType.INSUFFICIENT_STOCK)
    }
}
