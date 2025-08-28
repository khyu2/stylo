package project.stylo.web.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.common.s3.FileStorageService
import project.stylo.web.dao.CartDao
import project.stylo.web.dto.response.CartItemResponse

@Service
@Transactional
class CartService(
    private val cartDao: CartDao,
    private val fileStorageService: FileStorageService
) {
    fun getCartItems(memberId: Long): List<CartItemResponse> {
        val cartItems =  cartDao.findByMemberId(memberId)

        return cartItems.map {
            it.copy(
                thumbnailUrl = fileStorageService.getPresignedUrl(it.thumbnailUrl!!)
            )
        }
    }

    fun addToCart(memberId: Long, productId: Long, optionId: List<Long>?, quantity: Long) {
        cartDao.save(memberId, productId, optionId, quantity)
    }

    fun updateQuantity(memberId: Long, cartItemId: Long, quantity: Long) {
        cartDao.updateQuantity(memberId, cartItemId, quantity)
    }

    fun removeFromCart(memberId: Long, cartItemId: Long) {
        cartDao.delete(memberId, cartItemId)
    }

    fun clearCart(memberId: Long) {
        cartDao.deleteAll(memberId)
    }

    fun getCartItemCount(memberId: Long): Long {
        return cartDao.getCartItemCount(memberId) ?: 0L
    }
}
