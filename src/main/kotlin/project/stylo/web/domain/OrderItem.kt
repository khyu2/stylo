package project.stylo.web.domain

import java.math.BigDecimal

data class OrderItem(
    val orderItemId: Long,
    val orderId: Long,
    val productId: Long,
    val optionId: Long? = null,
    val quantity: Int,
    val price: BigDecimal
)
