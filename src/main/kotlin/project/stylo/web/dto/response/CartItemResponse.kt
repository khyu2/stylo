package project.stylo.web.dto.response

import java.math.BigDecimal

data class CartItemResponse(
    val cartItemId: Long,
    val productId: Long,
    val productOptionId: Long?,
    val quantity: Long,
    val name: String,
    val price: BigDecimal,
    val additionalPrice: BigDecimal,
    val thumbnailUrl: String?,
    val sku: String? = null,
    val stock: Long = 0,
) {
    fun getTotalPrice(): BigDecimal = (price + additionalPrice) * BigDecimal(quantity)

    fun getDisplayName(): String {
        return if (!sku.isNullOrBlank()) {
            "$name ($sku)"
        } else {
            name
        }
    }

    fun isOutOfStock(): Boolean = stock <= 0

    fun getAvailableQuantity(): Long = maxOf(0, stock)
}
