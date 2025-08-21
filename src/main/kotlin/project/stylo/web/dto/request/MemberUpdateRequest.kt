package project.stylo.web.dto.request

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Size

data class MemberUpdateRequest(
    val name: String? = null,

    val isMarketing: Boolean? = null,

    val currentPassword: String? = null,

    @field:Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    val newPassword: String? = null,

    @field:Size(min = 8, max = 20, message = "비밀번호 확인은 8자 이상 20자 이하로 입력해주세요.")
    val confirmPassword: String? = null,
) {
    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    fun isPasswordMatching(): Boolean = newPassword == confirmPassword
}
