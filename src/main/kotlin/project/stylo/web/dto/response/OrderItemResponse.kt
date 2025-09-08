package project.stylo.web.dto.response

import java.math.BigDecimal

data class OrderItemResponse(
    val name: String,
    val thumbnailUrl: String,
    val optionSku: String,
    val quantity: Long,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal,
)
