package project.stylo.web.dto.response

data class OrderDetailResponse(
    val payment: PaymentResponse,
    val order: OrderResponse,
    val orderItems: List<OrderItemResponse>,
    val buyer: MemberResponse?,
    val shipping: AddressResponse?
)
