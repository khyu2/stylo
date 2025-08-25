package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JProduct
import org.springframework.stereotype.Repository
import project.stylo.common.exception.BaseException
import project.stylo.common.exception.BaseExceptionType
import project.stylo.web.domain.Product
import project.stylo.web.dto.request.ProductRequest
import java.time.LocalDateTime

@Repository
class ProductDao(
    private val dsl: DSLContext
) {
    companion object {
        private val PRODUCT = JProduct.PRODUCT
    }

    fun save(memberId: Long, request: ProductRequest): Product {
        val id = dsl.insertInto(PRODUCT)
            .set(PRODUCT.CATEGORY_ID, request.category)
            .set(PRODUCT.NAME, request.name)
            .set(PRODUCT.DESCRIPTION, request.description)
            .set(PRODUCT.PRICE, request.price)
            .set(PRODUCT.STOCK, request.stock)
            .set(PRODUCT.CREATED_BY, memberId)
            .returning(PRODUCT.PRODUCT_ID)
            .fetchOne(PRODUCT.PRODUCT_ID)!!

        return findById(id) ?: throw BaseException(BaseExceptionType.INTERNAL_SERVER_ERROR)
    }

    fun findById(productId: Long): Product? =
        dsl.selectFrom(PRODUCT)
            .where(PRODUCT.PRODUCT_ID.eq(productId))
            .and(PRODUCT.DELETED_AT.isNull)
            .fetchOneInto(Product::class.java)

    fun findAll(): List<Product> =
        dsl.selectFrom(PRODUCT)
            .where(PRODUCT.DELETED_AT.isNull)
            .fetchInto(Product::class.java)

    fun existsByName(name: String): Boolean =
        dsl.fetchExists(
            dsl.selectOne()
                .from(PRODUCT)
                .where(PRODUCT.NAME.eq(name))
                .and(PRODUCT.DELETED_AT.isNull)
        )

    fun update(product: Product) =
        dsl.update(PRODUCT)
            .set(PRODUCT.CATEGORY_ID, product.categoryId)
            .set(PRODUCT.NAME, product.name)
            .set(PRODUCT.DESCRIPTION, product.description)
            .set(PRODUCT.PRICE, product.price)
            .set(PRODUCT.STOCK, product.stock)
            .set(PRODUCT.THUMBNAIL_URL, product.thumbnailUrl)
            .set(PRODUCT.UPDATED_AT, LocalDateTime.now())
            .where(PRODUCT.PRODUCT_ID.eq(product.productId))
            .and(PRODUCT.DELETED_AT.isNull)
            .execute()

    fun updateThumbnail(productId: Long, thumbnailUrl: String?) =
        dsl.update(PRODUCT)
            .set(PRODUCT.THUMBNAIL_URL, thumbnailUrl)
            .set(PRODUCT.UPDATED_AT, LocalDateTime.now())
            .where(PRODUCT.PRODUCT_ID.eq(productId))
            .and(PRODUCT.DELETED_AT.isNull)
            .execute()

    fun delete(productId: Long) =
        dsl.update(PRODUCT)
            .set(PRODUCT.DELETED_AT, LocalDateTime.now())
            .where(PRODUCT.PRODUCT_ID.eq(productId))
            .and(PRODUCT.DELETED_AT.isNull)
            .execute()

}
