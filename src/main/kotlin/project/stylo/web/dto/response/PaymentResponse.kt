package project.stylo.web.dto.response

import project.stylo.web.domain.Payment
import project.stylo.web.domain.enums.PaymentMethodType
import project.stylo.web.domain.enums.PaymentStatus
import project.stylo.web.domain.enums.PgProviderType
import java.math.BigDecimal

data class PaymentResponse(
    val paymentId: Long,
    val orderId: Long,
    val memberId: Long,
    val orderUid: String,
    val paymentKey: String?,
    val amount: BigDecimal,
    val currency: String,
    val method: PaymentMethodType,
    val pgProvider: PgProviderType,
    val status: PaymentStatus
) {
    companion object {
        fun from(payment: Payment): PaymentResponse {
            return PaymentResponse(
                paymentId = payment.paymentId!!,
                orderId = payment.orderId,
                memberId = payment.memberId,
                orderUid = payment.orderUid,
                paymentKey = payment.paymentKey,
                amount = payment.amount,
                currency = payment.currency,
                method = payment.method,
                pgProvider = payment.pgProvider,
                status = payment.status
            )
        }
    }
}
