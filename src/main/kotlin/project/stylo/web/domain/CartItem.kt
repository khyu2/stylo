package project.stylo.web.domain

data class CartItem(
    val cartItemId: Long,
    val cartId: Long,
    val productId: Long? = null,
    val optionId: Long? = null,
    val quantity: Int? = null
)