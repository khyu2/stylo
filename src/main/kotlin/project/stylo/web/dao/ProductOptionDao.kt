package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JProductOption
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class ProductOptionDao(private val dsl: DSLContext) {
    companion object {
        private val PRODUCT_OPTION = JProductOption.PRODUCT_OPTION
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
}