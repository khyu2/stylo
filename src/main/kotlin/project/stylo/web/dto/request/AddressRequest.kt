package project.stylo.web.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AddressRequest(
    @field:NotBlank(message = "수취인 이름은 필수입니다")
    @field:Size(max = 50, message = "수취인 이름은 50자 이하여야 합니다")
    val recipient: String,

    @field:NotBlank(message = "전화번호는 필수입니다")
    @field:Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다")
    val phone: String,

    @field:NotBlank(message = "주소는 필수입니다")
    @field:Size(max = 200, message = "주소는 200자 이하여야 합니다")
    val address: String,

    @field:Size(max = 100, message = "상세주소는 100자 이하여야 합니다")
    val addressDetail: String? = null,

    @field:NotBlank(message = "우편번호는 필수입니다")
    @field:Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다")
    val postalCode: String,

    val requestMessage: String,

    val defaultAddress: Boolean = false
)