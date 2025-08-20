package project.stylo.web.exception

import org.springframework.http.HttpStatus
import project.stylo.common.exception.ExceptionType

enum class MemberExceptionType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
): ExceptionType {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원이 존재하지 않습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER_ALREADY_EXISTS", "이미 존재하는 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "PASSWORD_NOT_MATCH", "비밀번호가 일치하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "현재 비밀번호가 올바르지 않습니다."),
    INVALID_FILE(HttpStatus.BAD_REQUEST, "INVALID_FILE", "올바르지 않은 파일입니다."),
}