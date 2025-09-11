package project.stylo.web.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.common.s3.FileStorageService
import project.stylo.web.dao.ProductDao
import project.stylo.web.dao.ProductOptionDao
import project.stylo.web.dao.WishlistDao
import project.stylo.web.domain.Member
import project.stylo.web.dto.response.WishlistItemResponse

@Service
@Transactional
class WishlistService(
    private val productDao: ProductDao,
    private val wishlistDao: WishlistDao,
    private val productOptionDao: ProductOptionDao,
    private val fileStorageService: FileStorageService,
) {
    fun add(member: Member, productId: Long) {
        if (wishlistDao.existsByMemberIdAndProductId(member.memberId!!, productId)) return
        wishlistDao.save(member.memberId, productId)
    }

    fun remove(member: Member, productId: Long) {
        if (!wishlistDao.existsByMemberIdAndProductId(member.memberId!!, productId)) return
        wishlistDao.deleteByMemberIdAndProductId(member.memberId, productId)
    }

    fun toggle(member: Member, productId: Long): Boolean {
        if (wishlistDao.existsByMemberIdAndProductId(member.memberId!!, productId)) {
            val wishlistId = wishlistDao.findByMemberIdAndProductId(member.memberId, productId)
            wishlistId?.let { remove(member, it.wishlistId) }
            return false
        } else {
            add(member, productId)
            return true
        }
    }

    @Transactional(readOnly = true)
    fun getItems(member: Member): List<WishlistItemResponse> {
        val wishlists = wishlistDao.findAllByMemberId(member.memberId!!)

        val productIds = wishlists.map { it.productId }
        val products = productDao.findByIds(productIds).values
        return products.map { product ->
            val options = product.productId.let { productOptionDao.findAllByProductId(it) }
            val available = options.any { it.stock > 0L }
            val thumb = product.thumbnailUrl?.let { fileStorageService.getPresignedUrl(it) }
            WishlistItemResponse(
                productId = product.productId,
                name = product.name,
                price = product.price,
                thumbnailUrl = thumb,
                available = available
            )
        }.sortedBy { it.name }
    }
}
