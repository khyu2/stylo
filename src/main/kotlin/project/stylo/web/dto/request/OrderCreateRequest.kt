package project.stylo.web.dto.request

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import project.stylo.web.domain.enums.PaymentMethodType

data class OrderItemRequest(
    val productOptionId: Long,
    val quantity: Long
)

data class OrderCreateRequest(
    var addressId: Long?,                 // 기존 배송지 ID (없으면 null)
    val addressRequest: AddressRequest?,  // 새 배송지 정보

    @field:NotNull(message = "결제수단은 필수입니다.")
    val paymentMethod: PaymentMethodType, // "CARD", "BANK_TRANSFER" 등

    @field:NotEmpty(message = "주문 상품 정보는 필수입니다.")
    val cartItems: List<OrderItemRequest> // 주문 상품 정보 포함
)