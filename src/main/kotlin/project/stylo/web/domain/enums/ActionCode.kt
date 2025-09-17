package project.stylo.web.domain.enums

import com.fasterxml.jackson.annotation.JsonCreator

/**
 * 사용자 액션 로그를 기록하기 위한 액션 코드
 */
enum class ActionCode(val code: String) {
    // products
    PRODUCT_CREATE("product.create"),
    PRODUCT_VIEW("product.view"),
    PRODUCT_UPDATE("product.update"),
    PRODUCT_DELETE("product.delete"),

    // cart
    CART_VIEW("cart.view"),
    CART_ADD("cart.add"),
    CART_UPDATE("cart.update"),
    CART_REMOVE("cart.remove"),
    CART_CLEAR("cart.clear"),

    // orders
    ORDERS_LIST("orders.list"),
    ORDERS_DETAIL("orders.detail"),
    ORDERS_CREATE_VIEW("orders.create.view"),
    ORDERS_CREATE("orders.create"),

    // payment
    PAYMENT_CONFIRM("payment.confirm"),
    PAYMENT_DETAIL("payment.detail"),
    ;

    override fun toString(): String = code

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromCode(code: String): ActionCode =
            ActionCode.entries.find { it.code == code }
                ?: throw IllegalArgumentException("Unknown ActionCode: $code")
    }
}