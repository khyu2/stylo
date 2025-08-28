package project.stylo.web.exception

import org.springframework.http.HttpStatus
import project.stylo.common.exception.ExceptionType

enum class OptionExceptionType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : ExceptionType {
    OPTION_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "OPT_001", "Option key not found"),
    OPTION_KEY_DUPLICATED(HttpStatus.BAD_REQUEST, "OPT_002", "Option key duplicated"),
    OPTION_VALUE_NOT_FOUND(HttpStatus.NOT_FOUND, "OPT_003", "Option value not found"),
    OPTION_VALUE_DUPLICATED(HttpStatus.BAD_REQUEST, "OPT_004", "Option value duplicated"),
    PRODUCT_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "OPT_005", "Product option not found"),
    PRODUCT_OPTION_DUPLICATED(HttpStatus.BAD_REQUEST, "OPT_006", "Product option duplicated"),
}