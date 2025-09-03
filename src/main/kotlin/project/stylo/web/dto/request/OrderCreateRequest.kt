package project.stylo.web.dto.request

import project.stylo.web.domain.enums.PaymentMethodType

data class OrderItemRequest(
    val productOptionId: Long,
    val quantity: Long
)

data class OrderCreateRequest(
    val addressId: Long?,                 // 기존 배송지 ID (없으면 null)
    val addressRequest: AddressRequest?,  // 새 배송지 정보
    val paymentMethod: PaymentMethodType, // "CARD", "BANK_TRANSFER" 등
    val cartItems: List<OrderItemRequest> // 주문 상품 정보 포함
)