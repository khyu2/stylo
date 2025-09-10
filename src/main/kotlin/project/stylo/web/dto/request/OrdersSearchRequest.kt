package project.stylo.web.dto.request

import project.stylo.web.domain.enums.OrderStatus
import project.stylo.web.domain.enums.PaymentStatus
import java.time.LocalDateTime

data class OrdersSearchRequest(
    val keyword: String? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val orderStatus: OrderStatus? = null,
    val paymentStatus: PaymentStatus? = null,
    val minPrice: Long? = null,
    val maxPrice: Long? = null,
)
