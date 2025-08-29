package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOptionValue
import org.springframework.stereotype.Repository
import project.stylo.common.exception.BaseException
import project.stylo.web.exception.OptionExceptionType

@Repository
class OptionValueDao(private val dsl: DSLContext) {
    companion object {
        private val OPTION_VALUE = JOptionValue.OPTION_VALUE
    }

    fun save(optionKeyId: Long, value: String): Long? {
        return dsl.insertInto(OPTION_VALUE)
            .set(OPTION_VALUE.OPTION_KEY_ID, optionKeyId)
            .set(OPTION_VALUE.VALUE, value)
            .returning(OPTION_VALUE.OPTION_VALUE_ID)
            .fetchOne(OPTION_VALUE.OPTION_VALUE_ID)
    }
}