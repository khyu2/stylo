package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOptionValue
import org.jooq.generated.tables.JOptionVariant
import org.jooq.generated.tables.JProduct
import org.jooq.generated.tables.JProductOption
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import project.stylo.common.exception.BaseException
import project.stylo.common.exception.BaseExceptionType
import project.stylo.common.utils.JooqUtils.Companion.andIfNotNull
import project.stylo.common.utils.JooqUtils.Companion.applySorting
import project.stylo.common.utils.JooqUtils.Companion.existsIfNotEmpty
import project.stylo.common.utils.JooqUtils.Companion.likeIgnoreCaseIfNotBlank
import project.stylo.web.domain.Product
import project.stylo.web.dto.request.ProductRequest
import project.stylo.web.dto.request.ProductSearchRequest
import java.time.LocalDateTime

@Repository
class ProductDao(
    private val dsl: DSLContext
) {
    companion object {
        private val PRODUCT = JProduct.PRODUCT
        private val PRODUCT_OPTION = JProductOption.PRODUCT_OPTION
        private val OPTION_VALUE = JOptionValue.OPTION_VALUE
        private val OPTION_VARIANT = JOptionVariant.OPTION_VARIANT
    }

    fun save(memberId: Long, request: ProductRequest): Product {
        val id = dsl.insertInto(PRODUCT)
            .set(PRODUCT.CATEGORY_ID, request.categoryId)
            .set(PRODUCT.NAME, request.name)
            .set(PRODUCT.DESCRIPTION, request.description)
            .set(PRODUCT.PRICE, request.price)
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

    fun findByIds(productIds: Collection<Long>): Map<Long, Product> =
        if (productIds.isEmpty()) emptyMap() else
            dsl.selectFrom(PRODUCT)
                .where(PRODUCT.PRODUCT_ID.`in`(productIds))
                .and(PRODUCT.DELETED_AT.isNull)
                .fetchInto(Product::class.java)
                .associateBy { it.productId }

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

    fun searchProducts(request: ProductSearchRequest, pageable: Pageable): Page<Product> {
        val baseQuery = dsl.selectDistinct(PRODUCT.asterisk())
            .from(PRODUCT)
            .leftJoin(PRODUCT_OPTION).on(PRODUCT.PRODUCT_ID.eq(PRODUCT_OPTION.PRODUCT_ID))
            .join(OPTION_VARIANT).on(OPTION_VARIANT.PRODUCT_OPTION_ID.eq(PRODUCT_OPTION.PRODUCT_OPTION_ID))
            .where(PRODUCT.DELETED_AT.isNull)
            .and(request.categoryId.andIfNotNull { PRODUCT.CATEGORY_ID.eq(it) })
            .and(
                request.keyword.likeIgnoreCaseIfNotBlank(PRODUCT.NAME)
                    .or(request.keyword.likeIgnoreCaseIfNotBlank(PRODUCT.DESCRIPTION))
            )
            .and(request.minPrice.andIfNotNull { PRODUCT.PRICE.greaterOrEqual(it) })
            .and(request.maxPrice.andIfNotNull { PRODUCT.PRICE.lessOrEqual(it) })
            .and(
                request.genderIds.existsIfNotEmpty(
                    dsl,
                    OPTION_VALUE,
                    OPTION_VALUE.OPTION_VALUE_ID,
                    OPTION_VARIANT.OPTION_VALUE_ID,
                    OPTION_VALUE.VALUE
                )
            )
            .and(
                request.sizeIds.existsIfNotEmpty(
                    dsl,
                    OPTION_VALUE,
                    OPTION_VALUE.OPTION_VALUE_ID,
                    OPTION_VARIANT.OPTION_VALUE_ID,
                    OPTION_VALUE.VALUE
                )
            )
            .and(
                request.colorIds.existsIfNotEmpty(
                    dsl,
                    OPTION_VALUE,
                    OPTION_VALUE.OPTION_VALUE_ID,
                    OPTION_VARIANT.OPTION_VALUE_ID,
                    OPTION_VALUE.VALUE
                )
            )

        // 전체 개수 조회
        val totalCount = dsl.fetchCount(baseQuery)

        // 정렬 적용
        val sortedQuery = if (pageable.sort.isSorted) {
            baseQuery.applySorting(pageable.sort) { property, isAscending ->
                when (property) {
                    "name" -> if (isAscending) PRODUCT.NAME.asc() else PRODUCT.NAME.desc()
                    "price" -> if (isAscending) PRODUCT.PRICE.asc() else PRODUCT.PRICE.desc()
                    else -> if (isAscending) PRODUCT.PRODUCT_ID.asc() else PRODUCT.PRODUCT_ID.desc()
                }
            }
        } else {
            baseQuery.orderBy(PRODUCT.CREATED_AT.desc())
        }

        // 페이징 적용
        val products = sortedQuery
            .limit(pageable.pageSize)
            .offset(pageable.offset)
            .fetchInto(Product::class.java)

        return PageImpl(products, pageable, totalCount.toLong())
    }
}
