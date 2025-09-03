package project.stylo.web.domain

import java.time.LocalDateTime

data class Address(
    val addressId: Long,
    val memberId: Long,
    val recipient: String,
    val phone: String,
    val address: String,
    val addressDetail: String? = null,
    val postalCode: String,
    val requestMessage: String? = null,
    val defaultAddress: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
)