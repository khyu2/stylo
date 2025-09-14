package project.stylo.web.controller

import org.json.simple.JSONObject
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import project.stylo.auth.resolver.Auth
import project.stylo.common.aop.LoggedAction
import project.stylo.web.domain.Member
import project.stylo.web.domain.enums.ActionCode
import project.stylo.web.domain.enums.EventType
import project.stylo.web.service.PaymentService

@Controller
@RequestMapping("/payment")
class PaymentController(
    private val paymentService: PaymentService,
) {
    @GetMapping("/success")
    fun paySuccess(): String = "payment/success"

    @GetMapping("/fail")
    fun payFail(): String = "payment/fail"

    // 결제 승인 요청 (PG 사로부터 결제 완료 후 호출)
    @PostMapping("/confirm")
    @LoggedAction(eventType = EventType.PAYMENT_CONFIRM, action = ActionCode.PAYMENT_CONFIRM, includeArgs = true)
    fun confirmPayment(@RequestBody jsonBody: String): ResponseEntity<JSONObject> =
        paymentService.confirmPayment(jsonBody)

    // 결제 상세 조회
    @GetMapping("/detail")
    @LoggedAction(eventType = EventType.VIEW, action = ActionCode.PAYMENT_DETAIL, includeArgs = true)
    fun paymentDetail(
        @Auth member: Member,
        @RequestParam paymentKey: String,
        model: Model
    ): String {
        paymentService.getPayment(paymentKey) ?: run {
            model.addAttribute("message", "유효하지 않은 결제 정보입니다")
            model.addAttribute("code", "PAYMENT_NOT_FOUND")
            return "payment/fail"
        }

        val detail = paymentService.getPaymentDetail(member, paymentKey)
        model.addAttribute("payment", detail.payment)
        model.addAttribute("order", detail.order)
        model.addAttribute("orderItems", detail.orderItems)
        model.addAttribute("buyer", detail.buyer)
        model.addAttribute("shipping", detail.shipping)
        return "payment/detail"
    }
}
