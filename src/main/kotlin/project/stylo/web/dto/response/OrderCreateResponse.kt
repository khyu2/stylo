package project.stylo.web.dto.response

import java.math.BigDecimal

data class OrderCreateResponse(
    val orderId: String,                   // 주문 식별자 (예: ORDER-1693838400000-1234)
    val orderName: String,                 // 결제창 표시용 - 주문명 (예: "스타일로 외 2건")
    val amount: BigDecimal,                // 총 결제 금액
    val customerName: String,              // 결제창 표시용 - 고객 이름
    val customerEmail: String,             // 결제창 표시용 - 고객 이메일
    val customerPhone: String,             // 결제창 표시용 - 고객 전화번호
) {
    val sanitizedPhone: String get() = customerPhone.replace("-", "")
}