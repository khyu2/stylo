package project.stylo.web.domain

import project.stylo.web.domain.enums.PaymentMethodType
import project.stylo.web.domain.enums.PaymentStatus
import project.stylo.web.domain.enums.PgProviderType
import java.math.BigDecimal
import java.time.LocalDateTime

data class Payment(
    val paymentId: Long? = null,
    val orderId: Long,
    val memberId: Long,
    val orderUid: String,
    val paymentKey: String? = null,
    val amount: BigDecimal,
    val currency: String,
    val method: PaymentMethodType,
    val pgProvider: PgProviderType,
    val transactionId: String? = null,
    val status: PaymentStatus,
    val approvedAt: LocalDateTime? = null,
    val canceledAt: LocalDateTime? = null,
    val failedAt: LocalDateTime? = null,
    val failReason: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
