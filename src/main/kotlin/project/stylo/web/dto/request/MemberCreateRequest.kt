package project.stylo.web.dto.request

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class MemberCreateRequest(
    @field:Email(message = "유효한 이메일 형식이어야 합니다.")
    @field:NotBlank(message = "이메일은 필수입니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    val password: String,

    @field:NotBlank(message = "전화번호는 필수입니다.")
    @field:Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "전화번호는 XXX-XXXX-XXXX 형식이어야 합니다.")
    val phone: String,

    @field:NotBlank(message = "비밀번호 확인은 필수입니다.")
    @field:Size(min = 8, message = "비밀번호 확인은 최소 8자 이상이어야 합니다.")
    val confirmPassword: String? = null,

    @field:NotBlank(message = "이름은 필수입니다.")
    val name: String,

    @field:NotNull(message = "개인정보처리방침은 필수 동의 항목입니다.")
    val isTerm: Boolean,

    val isMarketing: Boolean? = false
) {
    @get:AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    val passwordMatching: Boolean get() = password == confirmPassword
}
