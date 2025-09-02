package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JCartItem
import org.jooq.generated.tables.JProduct
import org.jooq.generated.tables.JProductOption
import org.springframework.stereotype.Repository
import project.stylo.web.domain.CartItem
import project.stylo.web.dto.response.CartItemResponse

@Repository
class CartDao(
    private val dsl: DSLContext
) {
    companion object {
        private val PRODUCT = JProduct.PRODUCT
        private val PRODUCT_OPTION = JProductOption.PRODUCT_OPTION
        private val CART_ITEM = JCartItem.CART_ITEM
    }

    fun findById(cartItemId: Long): CartItem? =
        dsl.selectFrom(CART_ITEM)
            .where(CART_ITEM.CART_ITEM_ID.eq(cartItemId))
            .fetchOneInto(CartItem::class.java)

    fun findByMemberId(memberId: Long): List<CartItemResponse> =
        dsl.select(
            CART_ITEM.CART_ITEM_ID.`as`("cartItemId"),
            PRODUCT.PRODUCT_ID.`as`("productId"),
            CART_ITEM.PRODUCT_OPTION_ID.`as`("productOptionId"),
            CART_ITEM.QUANTITY.`as`("quantity"),
            PRODUCT.NAME.`as`("name"),
            PRODUCT.PRICE.`as`("price"),
            PRODUCT_OPTION.ADDITIONAL_PRICE.`as`("additionalPrice"),
            PRODUCT.THUMBNAIL_URL.`as`("thumbnailUrl"),
            PRODUCT_OPTION.SKU.`as`("sku"),
            PRODUCT_OPTION.STOCK.`as`("stock")
        )
            .from(CART_ITEM)
            .join(PRODUCT_OPTION).on(PRODUCT_OPTION.PRODUCT_OPTION_ID.eq(CART_ITEM.PRODUCT_OPTION_ID))
            .join(PRODUCT).on(PRODUCT.PRODUCT_ID.eq(PRODUCT_OPTION.PRODUCT_ID))
            .where(CART_ITEM.MEMBER_ID.eq(memberId))
            .fetchInto(CartItemResponse::class.java)

    fun save(cartItem: CartItem) =
        dsl.insertInto(CART_ITEM)
            .set(CART_ITEM.MEMBER_ID, cartItem.memberId)
            .set(CART_ITEM.PRODUCT_OPTION_ID, cartItem.productOptionId)
            .set(CART_ITEM.QUANTITY, cartItem.quantity)
            .execute()

    fun updateQuantity(memberId: Long, cartItemId: Long, quantity: Long) =
        dsl.update(CART_ITEM)
            .set(CART_ITEM.QUANTITY, quantity)
            .where(CART_ITEM.CART_ITEM_ID.eq(cartItemId))
            .and(CART_ITEM.MEMBER_ID.eq(memberId))
            .execute()

    fun delete(memberId: Long, cartItemId: Long) =
        dsl.deleteFrom(CART_ITEM)
            .where(CART_ITEM.CART_ITEM_ID.eq(cartItemId))
            .and(CART_ITEM.MEMBER_ID.eq(memberId))
            .execute()

    fun deleteAll(memberId: Long) =
        dsl.deleteFrom(CART_ITEM)
            .where(CART_ITEM.MEMBER_ID.eq(memberId))
            .execute()

    fun getCartItemCount(memberId: Long): Long? =
        dsl.selectCount()
            .from(CART_ITEM)
            .where(CART_ITEM.MEMBER_ID.eq(memberId))
            .fetchOneInto(Long::class.java)

}
