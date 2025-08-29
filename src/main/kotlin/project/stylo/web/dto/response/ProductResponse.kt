package project.stylo.web.dto.response

import project.stylo.web.domain.Product
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductResponse(
    val productId: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stock: Long = 0,
    val productUrl: String,
    val options: List<ProductOptionResponse>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(product: Product, productUrl: String): ProductResponse {
            return ProductResponse(
                productId = product.productId,
                name = product.name,
                description = product.description,
                price = product.price,
                productUrl = productUrl,
                options = emptyList(),
                createdAt = product.createdAt,
                updatedAt = product.updatedAt
            )
        }
    }

    val totalStock: Long get() = options.sumOf { it.stock }
}
