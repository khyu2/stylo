package project.stylo.web.domain.enums

// toss 의 경우 응답값이 "카드", "가상계좌" 등 한글로 오기 때문에 한글로 매핑
enum class PaymentMethodType(val label: String) {
    CARD("카드"),
    BANK_TRANSFER("가상계좌"),
    SIMPLE_PAYMENT("간편결제"),
    MOBILE_PAYMENT("휴대폰"),
    ACCOUNT_TRANSFER("계좌이체"),
    CULTURE_GIFT_CERTIFICATE("문화상품권"),
    BOOK_GIFT_CERTIFICATE("도서문화상품권"),
    GAME_GIFT_CERTIFICATE("게임문화상품권");

    companion object {
        fun fromLabel(label: String): PaymentMethodType? {
            return PaymentMethodType.entries.find { it.label == label }
        }
    }
}