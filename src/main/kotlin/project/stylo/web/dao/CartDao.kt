package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JCartItem
import org.jooq.generated.tables.JProduct
import org.springframework.stereotype.Repository
import project.stylo.web.dto.response.CartItemResponse

@Repository
class CartDao(
    private val dsl: DSLContext
) {
    companion object {
        private val PRODUCT = JProduct.PRODUCT
        private val CART_ITEM = JCartItem.CART_ITEM
    }

    fun findByMemberId(memberId: Long): List<CartItemResponse> {
        return dsl
            .select(
                CART_ITEM.CART_ITEM_ID,
                CART_ITEM.PRODUCT_OPTION_ID,
                CART_ITEM.QUANTITY,
                PRODUCT.NAME,
                PRODUCT.PRICE,
                PRODUCT.THUMBNAIL_URL,
            )
            .from(CART_ITEM)
            .where(CART_ITEM.MEMBER_ID.eq(memberId))
            .fetchInto(CartItemResponse::class.java)
    }

    fun save(memberId: Long, productId: Long, optionIds: List<Long>?, quantity: Long) {
        if (optionIds.isNullOrEmpty()) {
            // 옵션이 없는 경우
            saveSingleItem(memberId, productId, null, quantity)
        } else {
            // 여러 옵션이 있는 경우, 각 옵션을 별도의 장바구니 아이템으로 저장
            optionIds.forEach { optionId ->
                saveSingleItem(memberId, productId, optionId, quantity)
            }
        }
    }

    private fun saveSingleItem(memberId: Long, productId: Long, optionId: Long?, quantity: Long) {
        // 기존 장바구니 아이템이 있는지 확인
        val existingItem = dsl
            .select(CART_ITEM.CART_ITEM_ID, CART_ITEM.QUANTITY)
            .from(CART_ITEM)
            .where(CART_ITEM.MEMBER_ID.eq(memberId))
            .fetchOne()

        if (existingItem != null) {
            // 기존 아이템이 있으면 수량 업데이트
            val currentQuantity = existingItem.getValue(CART_ITEM.QUANTITY)
            val cartItemId = existingItem.getValue(CART_ITEM.CART_ITEM_ID)

            dsl
                .update(CART_ITEM)
                .set(CART_ITEM.QUANTITY, currentQuantity?.plus(quantity))
                .where(CART_ITEM.CART_ITEM_ID.eq(cartItemId))
                .execute()
        } else {
            // 새 아이템 추가
            dsl
                .insertInto(CART_ITEM)
                .set(CART_ITEM.MEMBER_ID, memberId)
                .set(CART_ITEM.QUANTITY, quantity)
                .execute()
        }
    }

    fun updateQuantity(memberId: Long, cartItemId: Long, quantity: Long) {
        dsl
            .update(CART_ITEM)
            .set(CART_ITEM.QUANTITY, quantity)
            .where(CART_ITEM.CART_ITEM_ID.eq(cartItemId))
            .and(CART_ITEM.MEMBER_ID.eq(memberId))
            .execute()
    }

    fun delete(memberId: Long, cartItemId: Long) {
        dsl
            .deleteFrom(CART_ITEM)
            .where(CART_ITEM.CART_ITEM_ID.eq(cartItemId))
            .and(CART_ITEM.MEMBER_ID.eq(memberId))
            .execute()
    }

    fun deleteAll(memberId: Long) {
        dsl
            .deleteFrom(CART_ITEM)
            .where(CART_ITEM.MEMBER_ID.eq(memberId))
            .execute()
    }

    fun getCartItemCount(memberId: Long): Long? {
        return dsl
            .selectCount()
            .from(CART_ITEM)
            .where(CART_ITEM.MEMBER_ID.eq(memberId))
            .fetchOneInto(Long::class.java)
    }
}
