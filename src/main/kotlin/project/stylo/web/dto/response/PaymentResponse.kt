package project.stylo.web.dto.response

import project.stylo.web.domain.Address
import project.stylo.web.domain.Member
import project.stylo.web.domain.Orders
import project.stylo.web.domain.Payment

data class PaymentResponse(
    val payment: Payment,
    val order: Orders,
    val orderItems: List<OrderItemResponse>,
    val buyer: Member?,
    val shipping: Address?
)
