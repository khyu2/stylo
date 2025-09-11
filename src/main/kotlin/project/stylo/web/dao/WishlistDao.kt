package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JWishlist
import org.springframework.stereotype.Repository
import project.stylo.web.domain.Wishlist

@Repository
class WishlistDao(private val dsl: DSLContext) {
    companion object {
        private val WISHLIST = JWishlist.WISHLIST
    }

    fun save(memberId: Long, productId: Long) =
        dsl.insertInto(WISHLIST)
            .set(WISHLIST.MEMBER_ID, memberId)
            .set(WISHLIST.PRODUCT_ID, productId)
            .execute()

    fun findByMemberIdAndProductId(memberId: Long, productId: Long): Wishlist? =
        dsl.selectFrom(WISHLIST)
            .where(WISHLIST.MEMBER_ID.eq(memberId).and(WISHLIST.PRODUCT_ID.eq(productId)))
            .fetchOneInto(Wishlist::class.java)

    fun findAllByMemberId(memberId: Long): List<Wishlist> =
        dsl.selectFrom(WISHLIST)
            .where(WISHLIST.MEMBER_ID.eq(memberId))
            .fetchInto(Wishlist::class.java)

    fun existsByMemberIdAndProductId(memberId: Long, productId: Long): Boolean =
        dsl.fetchExists(
            dsl.selectOne()
                .from(WISHLIST)
                .where(WISHLIST.MEMBER_ID.eq(memberId).and(WISHLIST.PRODUCT_ID.eq(productId)))
        )

    fun deleteByMemberIdAndProductId(memberId: Long, productId: Long) =
        dsl.deleteFrom(WISHLIST)
            .where(WISHLIST.MEMBER_ID.eq(memberId).and(WISHLIST.PRODUCT_ID.eq(productId)))
            .execute()
}