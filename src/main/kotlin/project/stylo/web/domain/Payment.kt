package project.stylo.web.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class Payment(
    val paymentId: Long,
    val orderId: Long,
    val pgProvider: String,
    val amount: BigDecimal,
    val status: String,
    val transactionId: String? = null,
    val paidAt: LocalDateTime? = null
)