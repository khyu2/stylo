package project.stylo.web.controller

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import project.stylo.auth.resolver.Auth
import project.stylo.web.dao.PaymentDao
import project.stylo.web.domain.Member
import project.stylo.web.service.PaymentService
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

@Controller
@RequestMapping("/payment")
class PaymentController(
    @Value("\${payments.toss.secretKey:test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6}")
    private val widgetSecretKey: String,
    private val paymentDao: PaymentDao,
    private val paymentService: PaymentService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(PaymentController::class.java)
    }

    @GetMapping("/success")
    fun paySuccess(): String = "payment/success"

    @GetMapping("/fail")
    fun payFail(): String = "payment/fail"

    @GetMapping("/confirm")
    fun confirmPage(): String = "orders/create"

    @PostMapping("/confirm")
    fun confirmPayment(@RequestBody jsonBody: String): ResponseEntity<JSONObject> {
        val parser = JSONParser()
        val paymentKey: String
        val orderId: String
        val amount: String

        try {
            val requestData = parser.parse(jsonBody) as JSONObject
            paymentKey = requestData["paymentKey"] as String
            orderId = requestData["orderId"] as String
            amount = requestData["amount"] as String
        } catch (e: ParseException) {
            logger.warn("결제 확인 요청 바디 파싱 실패", e)
            val error = JSONObject().apply {
                put("message", "Invalid request body")
            }
            return ResponseEntity.badRequest().body(error)
        } catch (e: Exception) {
            logger.error("결제 확인 요청 바디 처리 중 오류", e)
            val error = JSONObject().apply {
                put("message", "Unexpected error")
            }
            return ResponseEntity.internalServerError().body(error)
        }

        val obj = JSONObject().apply {
            put("orderId", orderId)
            put("amount", amount)
            put("paymentKey", paymentKey)
        }

        val encodedBytes = Base64.getEncoder()
            .encode(("$widgetSecretKey:").toByteArray(StandardCharsets.UTF_8))
        val authorization = "Basic ${String(encodedBytes)}"

        val url = URL("https://api.tosspayments.com/v1/payments/confirm")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            setRequestProperty("Authorization", authorization)
            setRequestProperty("Content-Type", "application/json")
            requestMethod = "POST"
            doOutput = true
        }

        // Send body
        connection.outputStream.use { os ->
            os.write(obj.toString().toByteArray(StandardCharsets.UTF_8))
        }

        val code = connection.responseCode
        val isSuccess = code == 200

        val responseStream = if (isSuccess) connection.inputStream else connection.errorStream
        val jsonObject = responseStream.use { input ->
            InputStreamReader(input, StandardCharsets.UTF_8).use { reader ->
                parser.parse(reader) as JSONObject
            }
        }

        // 결제 승인 성공 시 paymentDao에 저장
        if (isSuccess) {
            try {
                val orderUid = jsonObject["orderId"] as String
                val paymentKey = jsonObject["paymentKey"] as String
                val transactionId = jsonObject["lastTransactionKey"] as String
                paymentDao.confirm(orderUid, paymentKey, transactionId)
            } catch (e: Exception) {
                logger.warn("결제 정보 저장 중 오류", e)
            }
        }

        return ResponseEntity.status(code).body(jsonObject)
    }

    // TODO: ResponseDTO 만들기, Service 분리
    @GetMapping("/detail")
    fun paymentDetail(
        @Auth member: Member,
        @RequestParam paymentKey: String,
        model: Model
    ): String {
        val payment = paymentDao.findByPaymentKey(paymentKey)
        if (payment == null) {
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
