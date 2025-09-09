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
    NO_IMAGE_PROVIDED(status = HttpStatus.BAD_REQUEST, code = "NO_IMAGE_PROVIDED", message = "최소 하나의 이미지를 제공해야 합니다."),
    OPTION_NAME_MISSING(status = HttpStatus.BAD_REQUEST, code = "OPTION_NAME_MISSING", message = "옵션명이 누락되었습니다."),
    OPTION_VALUE_MISSING(status = HttpStatus.BAD_REQUEST, code = "OPTION_VALUE_MISSING", message = "옵션값이 누락되었습니다."),
    PRODUCT_OPTION_NOT_FOUND(status = HttpStatus.NOT_FOUND, code = "PRODUCT_OPTION_NOT_FOUND", message = "상품 옵션을 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(status = HttpStatus.BAD_REQUEST, code = "INSUFFICIENT_STOCK", message = "재고가 부족합니다."),
    NO_COMBINATION_PROVIDED(status = HttpStatus.BAD_REQUEST, code = "NO_COMBINATION_PROVIDED", message = "최소 하나의 옵션 조합을 제공해야 합니다."),
}