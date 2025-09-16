package project.stylo.web.dto.response

import java.math.BigDecimal

data class OrderItemResponse(
    val productId: Long,
    val name: String,
    val thumbnailUrl: String,
    val optionSku: String,
    val quantity: Long,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal,
    val hasReview: Boolean,
)