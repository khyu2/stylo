package project.stylo.web.dto.response

import project.stylo.web.domain.Address

data class AddressResponse(
    val addressId: Long,
    val memberId: Long,
    val recipient: String,
    val phone: String,
    val address: String,
    val addressDetail: String?,
    val postalCode: String,
    val requestMessage: String?,
    val defaultAddress: Boolean
) {
    companion object {
        fun from(address: Address): AddressResponse {
            return AddressResponse(
                addressId = address.addressId,
                memberId = address.memberId,
                recipient = address.recipient,
                phone = address.phone,
                address = address.address,
                addressDetail = address.addressDetail,
                postalCode = address.postalCode,
                requestMessage = address.requestMessage,
                defaultAddress = address.defaultAddress
            )
        }
    }
}