package project.stylo.web.dto.response

import java.math.BigDecimal

data class CartItemResponse(
    val cartItemId: Long,
    val productId: Long,
    val optionId: Long?,
    val quantity: Int,
    val name: String,
    val price: BigDecimal,
    val thumbnailUrl: String?,
    val optionValue: String?,
    val optionTypeName: String?
) {
    fun getTotalPrice(): BigDecimal {
        return price.multiply(BigDecimal(quantity))
    }

    fun getDisplayName(): String {
        return if (optionValue != null && optionTypeName != null) {
            "$name ($optionTypeName: $optionValue)"
        } else {
            name
        }
    }
}
