package project.stylo.web.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.auth.utils.SecurityUtils
import project.stylo.common.exception.BaseException
import project.stylo.common.s3.FileStorageService
import project.stylo.web.dao.CartDao
import project.stylo.web.dao.ProductOptionDao
import project.stylo.web.domain.CartItem
import project.stylo.web.domain.Member
import project.stylo.web.dto.request.CartCreateRequest
import project.stylo.web.dto.request.CartUpdateRequest
import project.stylo.web.dto.response.CartItemResponse
import project.stylo.web.exception.CartExceptionType
import project.stylo.web.exception.ProductExceptionType

@Service
@Transactional
class CartService(
    private val cartDao: CartDao,
    private val productOptionDao: ProductOptionDao,
    private val fileStorageService: FileStorageService,
) {
    fun getCartItems(member: Member): List<CartItemResponse> {
        val cartItems = cartDao.findByMemberId(member.memberId!!)

        return cartItems.map { item ->
            val presignedUrl = item.thumbnailUrl?.let { fileStorageService.getPresignedUrl(it) }
            val sku = item.productOptionId?.let { productOptionDao.findById(it)?.sku }
            item.copy(
                thumbnailUrl = presignedUrl,
                sku = sku
            )
        }
    }

    fun addToCart(member: Member, request: CartCreateRequest) {
        // productOptionId가 null인 경우 기본 옵션을 찾음
        val finalProductOptionId = request.productOptionId ?: run {
            // 옵션이 없는 상품의 경우 기본 ProductOption을 찾음
            val defaultOptions = productOptionDao.findAllByProductId(request.productId)
            val defaultOption = defaultOptions.find { it.sku == "DEFAULT" }
                ?: throw BaseException(ProductExceptionType.PRODUCT_OPTION_NOT_FOUND)
            defaultOption.productOptionId
        }

        val productOption = productOptionDao.findById(finalProductOptionId)
            ?: throw BaseException(ProductExceptionType.PRODUCT_OPTION_NOT_FOUND)

        // 재고 체크
        if (request.quantity > productOption.stock) {
            throw BaseException(ProductExceptionType.INSUFFICIENT_STOCK)
        }

        val cartItem = CartItem(
            memberId = member.memberId!!,
            productOptionId = finalProductOptionId,
            quantity = request.quantity
        )

        cartDao.save(cartItem)

        SecurityUtils.updateCartCount((getCartItemCount(member.memberId)))
    }

    fun updateQuantity(member: Member, request: CartUpdateRequest) {
        val cartItem = cartDao.findById(request.cartItemId)
            ?: throw BaseException(CartExceptionType.CART_ITEM_NOT_FOUND)

        if (cartItem.memberId != member.memberId)
            throw BaseException(CartExceptionType.CART_ITEM_NOT_OWNED)

        // 재고 체크
        val productOption = productOptionDao.findById(cartItem.productOptionId)
            ?: throw BaseException(ProductExceptionType.PRODUCT_OPTION_NOT_FOUND)

        if (request.quantity > productOption.stock) {
            throw BaseException(ProductExceptionType.INSUFFICIENT_STOCK)
        }

        cartDao.updateQuantity(member.memberId, request.cartItemId, request.quantity)
    }

    fun removeFromCart(member: Member, cartItemId: Long) {
        val cartItem = cartDao.findById(cartItemId)
            ?: throw BaseException(CartExceptionType.CART_ITEM_NOT_FOUND)

        if (cartItem.memberId != member.memberId)
            throw BaseException(CartExceptionType.CART_ITEM_NOT_OWNED)

        cartDao.delete(member.memberId, cartItemId)

        SecurityUtils.updateCartCount((getCartItemCount(member.memberId)))
    }

    fun clearCart(member: Member) {
        cartDao.deleteAll(member.memberId!!)

        SecurityUtils.updateCartCount((getCartItemCount(member.memberId)))
    }

    fun getCartItemCount(memberId: Long): Long {
        return cartDao.getCartItemCount(memberId) ?: 0L
    }
}
