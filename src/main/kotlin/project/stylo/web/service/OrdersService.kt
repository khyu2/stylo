package project.stylo.web.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.common.exception.BaseException
import project.stylo.web.dao.AddressDao
import project.stylo.web.dao.CartDao
import project.stylo.web.dao.OrdersDao
import project.stylo.web.dao.ProductDao
import project.stylo.web.dao.ProductOptionDao
import project.stylo.web.domain.Member
import project.stylo.web.domain.Orders
import project.stylo.web.domain.enums.OrderStatus
import project.stylo.web.dto.request.OrderCreateRequest
import project.stylo.web.exception.CartExceptionType
import project.stylo.web.exception.MemberExceptionType
import project.stylo.web.exception.ProductExceptionType
import java.math.BigDecimal

@Service
@Transactional
class OrdersService(
    private val ordersDao: OrdersDao,
    private val addressDao: AddressDao,
    private val cartDao: CartDao,
    private val productDao: ProductDao,
    private val productOptionDao: ProductOptionDao,
) {
    // TODO: 주문 중복 검증, 결제 연동, 동시성 문제
    fun createOrder(member: Member, request: OrderCreateRequest) {
        val address = request.addressId?.let { addressDao.findById(it) }
            ?: throw BaseException(MemberExceptionType.ADDRESS_NOT_FOUND)

        if (request.cartItems.isEmpty())
            throw BaseException(CartExceptionType.CART_ITEM_NOT_FOUND)

        // 장바구니 상품과 상품 옵션 기반 상품 재고 검증
        var totalAmount: BigDecimal = BigDecimal.ZERO
        request.cartItems.forEach { cartItem ->
            val productOption = productOptionDao.findById(cartItem.productOptionId)
                ?: throw BaseException(ProductExceptionType.PRODUCT_OPTION_NOT_FOUND)

            productOption.validateStock(cartItem.quantity)

            val product = productDao.findById(productOption.productId)
                ?: throw BaseException(ProductExceptionType.PRODUCT_NOT_FOUND)

            val amount = (product.price + productOption.additionalPrice).toLong() * cartItem.quantity
            totalAmount += BigDecimal.valueOf(amount)
        }

        // 주문 생성
        ordersDao.save(
            Orders(
                memberId = member.memberId!!,
                addressId = address.addressId,
                totalAmount = totalAmount,
                status = OrderStatus.PENDING,
            )
        )

        // 장바구니 비우기
        cartDao.deleteAll(member.memberId)

        // 재고 차감
        request.cartItems.forEach { cartItem ->
            productOptionDao.decreaseStock(cartItem.productOptionId, cartItem.quantity).also {
                if (it == 0) throw BaseException(ProductExceptionType.INSUFFICIENT_STOCK) // 재고 부족
            }
        }

        // TODO: 결제 프로세스 연결
    }
}