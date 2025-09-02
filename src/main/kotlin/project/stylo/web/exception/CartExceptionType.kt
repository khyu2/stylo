package project.stylo.web.exception

import org.springframework.http.HttpStatus
import project.stylo.common.exception.ExceptionType

enum class CartExceptionType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : ExceptionType {
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CART-001", "장바구니 아이템을 찾을 수 없습니다."),
    CART_ITEM_NOT_OWNED(HttpStatus.FORBIDDEN, "CART-002", "장바구니 아이템에 대한 권한이 없습니다."),
}