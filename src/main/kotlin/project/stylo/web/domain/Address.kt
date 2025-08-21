package project.stylo.web.domain

data class Address(
    val addressId: Long,
    val memberId: Long,
    val recipient: String,
    val phone: String,
    val address: String,
    val addressDetail: String? = null,
    val postalCode: String,
    val defaultAddress: Boolean = false
)