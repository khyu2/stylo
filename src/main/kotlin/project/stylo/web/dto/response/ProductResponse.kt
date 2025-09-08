package project.stylo.web.dto.response

import project.stylo.web.domain.Product
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductResponse(
    val productId: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val categoryId: Long,
    val stock: Long = 0,
    val productUrl: String,
    val productImages: List<PresignedUrlResponse>? = emptyList(),
    val options: List<ProductOptionResponse>,
    val optionDefinitions: List<OptionDefinitionResponse> = emptyList(),
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
                categoryId = product.categoryId,
                productUrl = productUrl,
                productImages = emptyList(),
                options = emptyList(),
                optionDefinitions = emptyList(),
                createdAt = product.createdAt,
                updatedAt = product.updatedAt
            )
        }
    }

    val totalStock: Long get() = options.sumOf { it.stock }
}
