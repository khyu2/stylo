package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOptionVariant
import org.springframework.stereotype.Repository
import project.stylo.common.exception.BaseException
import project.stylo.web.exception.OptionExceptionType

@Repository
data class OptionVariantDao(private val dsl: DSLContext) {
    companion object {
        private val OPTION_VARIANT = JOptionVariant.OPTION_VARIANT
    }

    fun save(productOptionId: Long, optionValueId: Long): Long =
        dsl.insertInto(OPTION_VARIANT)
            .set(OPTION_VARIANT.PRODUCT_OPTION_ID, productOptionId)
            .set(OPTION_VARIANT.OPTION_VALUE_ID, optionValueId)
            .returning(OPTION_VARIANT.OPTION_VARIANT_ID)
            .fetchOneInto(Long::class.java) ?: throw BaseException(OptionExceptionType.OPTION_VALUE_DUPLICATED)
}
