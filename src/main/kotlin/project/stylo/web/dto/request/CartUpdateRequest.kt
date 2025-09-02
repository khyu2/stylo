package project.stylo.web.dto.request

import jakarta.validation.constraints.NotNull

data class CartUpdateRequest(
    @field:NotNull(message = "cartItemId는 필수입니다.")
    val cartItemId: Long,

    @field:NotNull(message = "수량은 필수입니다.")
    val quantity: Long
)
