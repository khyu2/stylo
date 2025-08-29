package project.stylo.web.dto.response

import project.stylo.web.domain.ProductOption
import java.math.BigDecimal

data class ProductOptionResponse(
    val productOptionId: Long,
    val sku: String,
    val additionalPrice: BigDecimal,
    val stock: Long,
    val optionValue: String
) {
    companion object {
        fun from(productOption: ProductOption, optionValue: String): ProductOptionResponse {
            return ProductOptionResponse(
                productOptionId = productOption.productOptionId,
                sku = productOption.sku,
                additionalPrice = productOption.additionalPrice,
                stock = productOption.stock,
                optionValue = optionValue
            )
        }
    }
}
