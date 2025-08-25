package project.stylo.web.exception

import org.springframework.http.HttpStatus
import project.stylo.common.exception.ExceptionType

enum class ProductExceptionType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
): ExceptionType {
    PRODUCT_NOT_FOUND(status = HttpStatus.NOT_FOUND, code = "PRODUCT_NOT_FOUND", message = "상품을 찾을 수 없습니다."),
    PRODUCT_DUPLICATED(status = HttpStatus.BAD_REQUEST, code = "PRODUCT_DUPLICATE", message = "이미 존재하는 상품명입니다."),
}