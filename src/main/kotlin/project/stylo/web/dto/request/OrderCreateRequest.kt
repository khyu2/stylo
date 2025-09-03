package project.stylo.web.dto.request

data class OrderItemRequest(
    val productOptionId: Long,
    val quantity: Int
)

data class OrderCreateRequest(
    val memberId: Long,
    val addressId: Long?,              // 기존 배송지 ID (없으면 null)
    val recipient: String?,            // 새 배송지 입력 시
    val phone: String?,
    val postalCode: String?,
    val address: String?,
    val addressDetail: String?,
    val requestMessage: String?,
    val paymentMethod: String,         // "CARD", "BANK_TRANSFER" 등
    val cartItems: List<OrderItemRequest> // 주문 상품 정보 포함
)