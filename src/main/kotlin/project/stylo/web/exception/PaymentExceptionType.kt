package project.stylo.web.exception

import org.springframework.http.HttpStatus
import project.stylo.common.exception.ExceptionType

enum class PaymentExceptionType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
): ExceptionType {
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_01", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PAYMENT_02", "결제 정보에 접근할 수 없습니다."),
}