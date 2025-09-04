package project.stylo.web.controller

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Base64

@Controller
@RequestMapping("/payment")
class PaymentController(
    @Value("\${payments.toss.secretKey:test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6}")
    private val widgetSecretKey: String,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(PaymentController::class.java)
    }

    @GetMapping("/success")
    fun paySuccess(): String = "payment/success"

    @GetMapping("/fail")
    fun payFail(): String = "payment/fail"

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

        return ResponseEntity.status(code).body(jsonObject)
    }
}
