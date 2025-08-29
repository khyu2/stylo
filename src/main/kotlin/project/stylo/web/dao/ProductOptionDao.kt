package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JProductOption
import org.springframework.stereotype.Repository
import project.stylo.common.exception.BaseException
import project.stylo.web.domain.ProductOption
import project.stylo.web.dto.request.OptionCombination
import project.stylo.web.exception.OptionExceptionType

@Repository
class ProductOptionDao(private val dsl: DSLContext) {
    companion object {
        private val PRODUCT_OPTION = JProductOption.PRODUCT_OPTION
    }

    fun save(request: OptionCombination, productId: Long): Long =
        dsl.insertInto(PRODUCT_OPTION)
            .set(PRODUCT_OPTION.PRODUCT_ID, productId)
            .set(PRODUCT_OPTION.SKU, request.sku)
            .set(PRODUCT_OPTION.ADDITIONAL_PRICE, request.additionalPrice)
            .set(PRODUCT_OPTION.STOCK, request.stock)
            .returning(PRODUCT_OPTION.PRODUCT_OPTION_ID)
            .fetchOne(PRODUCT_OPTION.PRODUCT_OPTION_ID)
            ?: throw BaseException(OptionExceptionType.PRODUCT_OPTION_DUPLICATED)

    fun findAllByProductId(productId: Long): List<ProductOption> =
        dsl.selectFrom(PRODUCT_OPTION)
            .where(PRODUCT_OPTION.PRODUCT_ID.eq(productId))
            .fetchInto(ProductOption::class.java)
}