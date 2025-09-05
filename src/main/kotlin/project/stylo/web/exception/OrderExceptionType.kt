package project.stylo.web.exception

import org.springframework.http.HttpStatus
import project.stylo.common.exception.ExceptionType

enum class OrderExceptionType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
): ExceptionType {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD_001", "주문 정보를 찾을 수 없습니다."),
}