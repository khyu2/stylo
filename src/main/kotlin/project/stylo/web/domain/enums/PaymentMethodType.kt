package project.stylo.web.domain.enums

/**
 * 토스페이먼츠 결제수단 타입 Enum
 * @see <a href="https://docs.tosspayments.com/codes/enum-codes">토스페이먼츠 ENUM 공식문서</a>
 */
enum class PaymentMethodType(val label: String) {
    CARD("카드"),
    VIRTUAL_ACCOUNT("가상계좌"),
    MOBILE_PHONE("휴대폰"),
    TRANSFER("계좌이체"),
    CULTURE_GIFT_CERTIFICATE("문화상품권"),
    BOOK_GIFT_CERTIFICATE("도서상품권"),
    GAME_GIFT_CERTIFICATE("게임상품권"),

    // 간편결제
    TOSSPAY("토스페이"),
    NAVERPAY("네이버페이"),
    SAMSUNGPAY("삼성페이"),
    KAKAOPAY("카카오페이"),
    PAYCO("페이코"),
    APPLEPAY("애플페이");

    companion object {
        fun fromLabel(label: String): PaymentMethodType? {
            return PaymentMethodType.entries.find { it.label == label }
        }
    }
}