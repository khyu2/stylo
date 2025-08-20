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
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE_NOT_FOUND", "프로필 이미지가 존재하지 않습니다."),
}