package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOption
import org.jooq.generated.tables.JOptionType
import org.jooq.generated.tables.JProductOption
import org.springframework.stereotype.Repository
import project.stylo.web.domain.ProductOption
import java.math.BigDecimal

@Repository
class ProductOptionDao(private val dsl: DSLContext) {
    companion object {
        private val PRODUCT_OPTION = JProductOption.PRODUCT_OPTION
        private val OPTION = JOption.OPTION
        private val OPTION_TYPE = JOptionType.OPTION_TYPE
    }

    fun save(productId: Long, optionId: Long, extraPrice: BigDecimal, stock: Long) {
        dsl.insertInto(PRODUCT_OPTION)
            .set(PRODUCT_OPTION.PRODUCT_ID, productId)
            .set(PRODUCT_OPTION.OPTION_ID, optionId)
            .set(PRODUCT_OPTION.EXTRA_PRICE, extraPrice)
            .set(PRODUCT_OPTION.STOCK, stock)
            .execute()
    }

    fun saveAll(productId: Long, optionIds: List<Long>) =
        dsl.batch(
            optionIds.map { optionId ->
                dsl.insertInto(PRODUCT_OPTION)
                    .set(PRODUCT_OPTION.PRODUCT_ID, productId)
                    .set(PRODUCT_OPTION.OPTION_ID, optionId)
            }
        ).execute()

    fun deleteByProductId(productId: Long) {
        dsl.deleteFrom(PRODUCT_OPTION)
            .where(PRODUCT_OPTION.PRODUCT_ID.eq(productId))
            .execute()
    }

    fun findByProductId(productId: Long): List<ProductOption> {
        return dsl.selectFrom(PRODUCT_OPTION)
            .where(PRODUCT_OPTION.PRODUCT_ID.eq(productId))
            .fetchInto(ProductOption::class.java)
    }

    fun findProductOptionsWithDetails(productId: Long): List<Map<String, Any>> {
        return dsl.select(
            PRODUCT_OPTION.PRODUCT_OPTION_ID,
            PRODUCT_OPTION.PRODUCT_ID,
            PRODUCT_OPTION.OPTION_ID,
            PRODUCT_OPTION.EXTRA_PRICE,
            PRODUCT_OPTION.STOCK,
            OPTION.VALUE,
            OPTION_TYPE.NAME.`as`("optionTypeName")
        )
            .from(PRODUCT_OPTION)
            .join(OPTION).on(PRODUCT_OPTION.OPTION_ID.eq(OPTION.OPTION_ID))
            .join(OPTION_TYPE).on(OPTION.OPTION_TYPE_ID.eq(OPTION_TYPE.OPTION_TYPE_ID))
            .where(PRODUCT_OPTION.PRODUCT_ID.eq(productId))
            .fetch()
            .map { record ->
                mapOf(
                    "productOptionId" to record.get(PRODUCT_OPTION.PRODUCT_OPTION_ID),
                    "productId" to record.get(PRODUCT_OPTION.PRODUCT_ID),
                    "optionId" to record.get(PRODUCT_OPTION.OPTION_ID),
                    "extraPrice" to record.get(PRODUCT_OPTION.EXTRA_PRICE),
                    "stock" to record.get(PRODUCT_OPTION.STOCK),
                    "optionValue" to record.get(OPTION.VALUE),
                    "optionTypeName" to record.get("optionTypeName")
                ) as Map<String, Any>?
            }
    }
}