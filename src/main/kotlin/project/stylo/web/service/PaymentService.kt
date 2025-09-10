package project.stylo.web.service

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.common.exception.BaseException
import project.stylo.common.s3.FileStorageService
import project.stylo.web.dao.AddressDao
import project.stylo.web.dao.MemberDao
import project.stylo.web.dao.OrderItemDao
import project.stylo.web.dao.OrdersDao
import project.stylo.web.dao.PaymentDao
import project.stylo.web.dao.ProductDao
import project.stylo.web.dao.ProductOptionDao
import project.stylo.web.domain.Member
import project.stylo.web.domain.Payment
import project.stylo.web.domain.enums.OrderStatus
import project.stylo.web.dto.response.OrderItemResponse
import project.stylo.web.dto.response.PaymentResponse
import project.stylo.web.exception.OrderExceptionType
import project.stylo.web.exception.PaymentExceptionType
import java.io.InputStreamReader
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

@Service
@Transactional
class PaymentService(
    private val paymentDao: PaymentDao,
    private val ordersDao: OrdersDao,
    private val orderItemDao: OrderItemDao,
    private val productOptionDao: ProductOptionDao,
    private val productDao: ProductDao,
    private val addressDao: AddressDao,
    private val memberDao: MemberDao,
    private val fileStorageService: FileStorageService,
    @Value("\${payments.toss.secretKey:test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6}")
    private val widgetSecretKey: String,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(PaymentService::class.java)
    }

    @Transactional(readOnly = true)
    fun getPayment(paymentKey: String): Payment? = paymentDao.findByPaymentKey(paymentKey)

    @Transactional(readOnly = true)
    fun getPaymentDetail(member: Member, paymentKey: String): PaymentResponse {
        val payment = paymentDao.findByPaymentKey(paymentKey)
            ?: throw BaseException(PaymentExceptionType.PAYMENT_NOT_FOUND)

        if (payment.memberId != member.memberId) {
            throw BaseException(PaymentExceptionType.PAYMENT_ACCESS_DENIED)
        }

        val order = ordersDao.findById(payment.orderId)
            ?: throw BaseException(OrderExceptionType.ORDER_NOT_FOUND)

        val items = orderItemDao.findByOrderId(order.orderId!!)
        val optionIds = items.map { it.productOptionId }.toSet()
        val optionMap = if (optionIds.isNotEmpty()) productOptionDao.findByIds(optionIds) else emptyMap()
        val productIds = optionMap.values.map { it.productId }.toSet()
        val productMap = if (productIds.isNotEmpty()) productDao.findByIds(productIds) else emptyMap()

        val enrichedItems = items.map { oi ->
            val opt = optionMap[oi.productOptionId]
            val prod = opt?.let { productMap[it.productId] }
            val presignedUrl = fileStorageService.getPresignedUrl(prod?.thumbnailUrl!!)
            OrderItemResponse(
                name = prod.name,
                thumbnailUrl = presignedUrl,
                optionSku = opt.sku,
                quantity = oi.quantity,
                unitPrice = oi.price,
                totalPrice = oi.price.multiply(BigDecimal.valueOf(oi.quantity))
            )
        }

        val address = addressDao.findById(order.addressId)
        val buyer = memberDao.findById(order.memberId)

        return PaymentResponse(
            payment = payment,
            order = order,
            orderItems = enrichedItems,
            buyer = buyer,
            shipping = address
        )
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
