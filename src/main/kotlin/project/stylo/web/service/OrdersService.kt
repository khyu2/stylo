package project.stylo.web.service

import jakarta.servlet.http.HttpSession
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.auth.utils.SecurityUtils
import project.stylo.common.exception.BaseException
import project.stylo.web.dao.AddressDao
import project.stylo.web.dao.CartDao
import project.stylo.web.dao.OrderItemDao
import project.stylo.web.dao.OrdersDao
import project.stylo.web.dao.PaymentDao
import project.stylo.web.dao.ProductDao
import project.stylo.web.dao.ProductOptionDao
import project.stylo.web.domain.Member
import project.stylo.web.domain.OrderItem
import project.stylo.web.domain.Orders
import project.stylo.web.domain.Payment
import project.stylo.web.domain.enums.OrderStatus
import project.stylo.web.domain.enums.PaymentStatus
import project.stylo.web.domain.enums.PgProviderType
import project.stylo.web.dto.request.OrderCreateRequest
import project.stylo.web.dto.request.OrdersSearchRequest
import project.stylo.web.dto.response.OrderCreateResponse
import project.stylo.web.dto.response.OrderResponse
import project.stylo.web.exception.MemberExceptionType
import project.stylo.web.exception.ProductExceptionType
import java.math.BigDecimal

@Service
@Transactional
class OrdersService(
    private val cartDao: CartDao,
    private val addressDao: AddressDao,
    private val ordersDao: OrdersDao,
    private val orderItemDao: OrderItemDao,
    private val productDao: ProductDao,
    private val productOptionDao: ProductOptionDao,
    private val paymentDao: PaymentDao
) {
    // TODO: 주문 중복 검증, 재고 동시성 문제, 주문/결제 트랜잭션 분리, 주문 취소/환불 처리
    fun createOrder(member: Member, request: OrderCreateRequest, session: HttpSession): OrderCreateResponse {
        val address = request.addressId?.let { addressDao.findById(it) }
            ?: throw BaseException(MemberExceptionType.ADDRESS_NOT_FOUND)

        // 상품 옵션 존재 여부 검증
        val optionIds = request.cartItems.map { it.productOptionId }
        val optionMap = productOptionDao.findByIds(optionIds)
        if (optionMap.size != optionIds.toSet().size) {
            throw BaseException(ProductExceptionType.PRODUCT_OPTION_NOT_FOUND)
        }

        // 옵션별 수량 합산 후 재고 검증
        val aggregatedQty = request.cartItems.groupBy { it.productOptionId }
            .mapValues { entry -> entry.value.sumOf { it.quantity } }
        aggregatedQty.forEach { (optionId, totalQty) ->
            val option = optionMap[optionId]!!
            option.validateStock(totalQty)
        }

        // 상품 존재 여부 검증
        val productIds = optionMap.values.map { it.productId }.toSet()
        val productMap = productDao.findByIds(productIds)
        if (productMap.size != productIds.size) {
            throw BaseException(ProductExceptionType.PRODUCT_NOT_FOUND)
        }

        // 상품 아이템 및 총 주문 금액 계산
        val orderItemPairs = request.cartItems.map { cartItem ->
            val option = optionMap[cartItem.productOptionId]!!
            val product = productMap[option.productId]!!
            val price = product.price + option.additionalPrice
            OrderItem(
                orderId = null, // 주문 생성 후 할당
                productOptionId = cartItem.productOptionId,
                quantity = cartItem.quantity,
                price = price,
            ) to (price.toLong() * cartItem.quantity)
        }

        // 금액 합산
        val totalAmount = orderItemPairs.fold(BigDecimal.ZERO) { acc, pair ->
            acc + BigDecimal.valueOf(pair.second)
        }

        // 주문 생성
        val orderId = ordersDao.save(
            Orders(
                memberId = member.memberId!!,
                addressId = address.addressId,
                totalAmount = totalAmount,
                status = OrderStatus.PENDING,
            )
        )

        // 주문 상품 생성
        val items = orderItemPairs.map { (orderItem, _) -> orderItem.copy(orderId = orderId) }
        orderItemDao.saveAll(orderId, items)

        // 장바구니 비우기
        cartDao.deleteAll(member.memberId)
        SecurityUtils.updateCartCount(0L)

        // 재고 차감
        val affected = productOptionDao.decreaseStockInBatch(aggregatedQty)
        if (affected != aggregatedQty.size) {
            throw BaseException(ProductExceptionType.INSUFFICIENT_STOCK)
        }

        // 결제 생성
        val orderUuid = "ORDER-${System.currentTimeMillis()}-${(1000..9999).random()}"
        paymentDao.save(
            Payment(
                orderId = orderId,
                memberId = member.memberId,
                orderUid = orderUuid,
                amount = totalAmount,
                currency = "KRW",
                method = request.paymentMethod,
                pgProvider = PgProviderType.TOSS,
                status = PaymentStatus.READY
            )
        )

        session.setAttribute("orderId", orderUuid)
        session.setAttribute("totalAmount", totalAmount)

        val orderName = if (items.size == 1)
            productMap[optionMap[items[0].productOptionId]!!.productId]!!.name
        else
            "${productMap[optionMap[items[0].productOptionId]!!.productId]!!.name} 외 ${items.size - 1}건"
        return OrderCreateResponse(
            orderId = orderUuid,
            orderName = orderName,
            amount = totalAmount,
            customerName = member.name,
            customerEmail = member.email,
            customerPhone = member.phone!!,
        )
    }

    @Transactional(readOnly = true)
    fun getOrders(request: OrdersSearchRequest, pageable: Pageable): Page<OrderResponse> =
        ordersDao.findAll(request, pageable)

    @Transactional(readOnly = true)
    fun getOrdersByMember(member: Member, pageable: Pageable): Page<OrderResponse> =
        ordersDao.findAllByMemberId(member.memberId!!, pageable)

    fun updateStatus(orderId: Long, status: OrderStatus) {
        ordersDao.updateStatus(orderId, status)
    }
}