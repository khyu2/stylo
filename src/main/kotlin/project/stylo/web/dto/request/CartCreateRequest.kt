package project.stylo.web.dto.request

import jakarta.validation.constraints.NotNull

data class CartCreateRequest(
    @field:NotNull(message = "상품 ID는 필수입니다.")
    val productId: Long,
    
    val productOptionId: Long? = null,

    @field:NotNull(message = "수량은 필수입니다.")
    val quantity: Long
)
