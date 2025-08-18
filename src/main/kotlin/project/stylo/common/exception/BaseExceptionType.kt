package project.stylo.common.exception

import org.springframework.http.HttpStatus

enum class BaseExceptionType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : ExceptionType {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근이 금지된 리소스입니다."),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", "아이디 또는 비밀번호가 잘못되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
}