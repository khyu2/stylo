package project.stylo.web.service

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.web.dao.OrdersDao
import project.stylo.web.dao.PaymentDao
import project.stylo.web.domain.enums.OrderStatus
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

@Service
@Transactional
class PaymentService(
    private val paymentDao: PaymentDao,
    private val ordersDao: OrdersDao,
    @Value("\${payments.toss.secretKey:test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6}")
    private val widgetSecretKey: String,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(PaymentService::class.java)
    }

    fun confirmPayment(jsonBody: String): ResponseEntity<JSONObject> {
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
            val error = JSONObject().apply { put("message", "Invalid request body") }
            return ResponseEntity.badRequest().body(error)
        } catch (e: Exception) {
            logger.error("결제 확인 요청 바디 처리 중 오류", e)
            val error = JSONObject().apply { put("message", "Unexpected error") }
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

        if (isSuccess) {
            try {
                val orderUid = jsonObject["orderId"] as String
                val paymentKeyRes = jsonObject["paymentKey"] as String
                val transactionId = jsonObject["lastTransactionKey"] as String
                val orderId = paymentDao.confirm(orderUid, paymentKeyRes, transactionId)

                jsonObject.put("orderId", orderId)

                // 주문 상태를 결제 완료로 변경
                ordersDao.updateStatus(orderId, OrderStatus.PAID)

                // TODO: 관리자 알림톡 전송

            } catch (e: Exception) {
                logger.warn("결제 정보 저장 중 오류", e)
            }
        }

        return ResponseEntity.status(code).body(jsonObject)
    }
}
