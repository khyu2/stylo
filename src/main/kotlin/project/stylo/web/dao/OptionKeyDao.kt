package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOptionKey
import org.springframework.stereotype.Repository
import project.stylo.common.exception.BaseException
import project.stylo.web.exception.OptionExceptionType

@Repository
class OptionKeyDao(private val dsl: DSLContext) {
    companion object {
        private val OPTION_KEY = JOptionKey.OPTION_KEY
    }

    fun save(productId: Long, name: String): Long =
        dsl.insertInto(OPTION_KEY)
            .set(OPTION_KEY.PRODUCT_ID, productId)
            .set(OPTION_KEY.NAME, name)
            .returning(OPTION_KEY.OPTION_KEY_ID)
            .fetchOneInto(Long::class.java) ?: throw BaseException(OptionExceptionType.OPTION_KEY_DUPLICATED)

}