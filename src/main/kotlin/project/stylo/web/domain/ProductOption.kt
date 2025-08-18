package project.stylo.web.domain

import java.math.BigDecimal

data class ProductOption(
    val optionId: Long,
    val productId: Long,
    val optionName: String,
    val additionalPrice: BigDecimal? = null,
    val stock: Int
)