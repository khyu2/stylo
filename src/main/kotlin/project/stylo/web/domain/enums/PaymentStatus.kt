package project.stylo.web.domain.enums

enum class PaymentStatus {
    READY, // 결제 준비
    IN_PROGRESS, // 결제 진행 중
    DONE, // 결제 완료
    FAILED, // 결제 실패
    CANCELED, // 결제 취소
    REFUNDED; // 환불 완료
}