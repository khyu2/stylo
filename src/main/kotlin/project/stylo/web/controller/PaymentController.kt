package project.stylo.web.controller

import org.json.simple.JSONObject
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import project.stylo.common.aop.LoggedAction
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

}
