package project.stylo.web.service

import org.springframework.stereotype.Service
import project.stylo.common.exception.BaseException
import project.stylo.web.dao.AddressDao
import project.stylo.web.dao.MemberDao
import project.stylo.web.dao.OrderItemDao
import project.stylo.web.dao.OrdersDao
import project.stylo.web.dao.PaymentDao
import project.stylo.web.dao.ProductDao
import project.stylo.web.dao.ProductOptionDao
import project.stylo.web.domain.Address
import project.stylo.web.domain.Member
import project.stylo.web.domain.Orders
import project.stylo.web.domain.Payment
import project.stylo.web.exception.OrderExceptionType
import project.stylo.web.exception.PaymentExceptionType
import java.math.BigDecimal

@Service
class PaymentService(
    private val paymentDao: PaymentDao,
    private val ordersDao: OrdersDao,
    private val orderItemDao: OrderItemDao,
    private val productOptionDao: ProductOptionDao,
    private val productDao: ProductDao,
    private val addressDao: AddressDao,
    private val memberDao: MemberDao,
) {
    data class OrderItemView(
        val name: String,
        val thumbnailUrl: String,
        val optionSku: String,
        val quantity: Long,
        val unitPrice: BigDecimal,
        val totalPrice: BigDecimal,
    )

    data class PaymentDetailView(
        val payment: Payment,
        val order: Orders,
        val orderItems: List<OrderItemView>,
        val buyer: Member?,
        val shipping: Address?
    )

    fun getPaymentDetail(member: Member, paymentKey: String): PaymentDetailView {
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
            OrderItemView(
                name = prod?.name ?: "상품",
                thumbnailUrl = prod?.thumbnailUrl ?: "/images/default_product.jpeg",
                optionSku = opt?.sku ?: "-",
                quantity = oi.quantity,
                unitPrice = oi.price,
                totalPrice = oi.price.multiply(BigDecimal.valueOf(oi.quantity.toLong()))
            )
        }

        val address = addressDao.findById(order.addressId)
        val buyer = memberDao.findById(order.memberId)

        return PaymentDetailView(
            payment = payment,
            order = order,
            orderItems = enrichedItems,
            buyer = buyer,
            shipping = address
        )
    }
}
