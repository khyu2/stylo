package project.stylo.web.dto.response

import java.math.BigDecimal

data class WishlistItemResponse(
    val productId: Long,
    val name: String,
    val price: BigDecimal,
    val thumbnailUrl: String?,
    val available: Boolean
)
