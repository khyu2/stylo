package project.stylo.web.dto.response

import java.math.BigDecimal

data class ProductOptionResponse(
    val productOptionId: Long,
    val productId: Long,
    val optionId: Long,
    val extraPrice: BigDecimal,
    val stock: Int,
    val optionValue: String,
    val optionTypeName: String
)
