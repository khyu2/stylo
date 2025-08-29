package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOptionValue
import org.jooq.generated.tables.JOptionVariant
import org.springframework.stereotype.Repository

@Repository
class OptionValueDao(private val dsl: DSLContext) {
    companion object {
        private val OPTION_VALUE = JOptionValue.OPTION_VALUE
        private val OPTION_VARIANT = JOptionVariant.OPTION_VARIANT
    }

    fun saveOrGetId(optionKeyId: Long, value: String): Long? {
        val existsId = dsl.select(OPTION_VALUE.OPTION_VALUE_ID)
            .from(OPTION_VALUE)
            .where(OPTION_VALUE.OPTION_KEY_ID.eq(optionKeyId))
            .and(OPTION_VALUE.VALUE.eq(value))
            .fetchOneInto(Long::class.java)

        if (existsId != null) return existsId

        return dsl.insertInto(OPTION_VALUE)
            .set(OPTION_VALUE.OPTION_KEY_ID, optionKeyId)
            .set(OPTION_VALUE.VALUE, value)
            .returning(OPTION_VALUE.OPTION_VALUE_ID)
            .fetchOne(OPTION_VALUE.OPTION_VALUE_ID)
    }

    fun findAllByProductOptionId(productOptionId: Long): List<String> =
        dsl.select(OPTION_VALUE.VALUE)
            .from(OPTION_VALUE)
            .join(OPTION_VARIANT).on(OPTION_VALUE.OPTION_VALUE_ID.eq(OPTION_VARIANT.OPTION_VALUE_ID))
            .where(OPTION_VARIANT.PRODUCT_OPTION_ID.eq(productOptionId))
            .fetchInto(String::class.java)
}