package project.stylo.auth.exception

import org.springframework.http.HttpStatus
import project.stylo.common.exception.ExceptionType

enum class AuthExceptionType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : ExceptionType {
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "AUTH_001", "지원하지 않는 소셜 로그인 제공자입니다."),
}