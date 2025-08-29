package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOptionKey
import org.springframework.stereotype.Repository

@Repository
class OptionKeyDao(private val dsl: DSLContext) {
    companion object {
        private val OPTION_KEY = JOptionKey.OPTION_KEY
    }

    fun saveOrGetId(productId: Long, name: String): Long? {
        val existsId = dsl.select(OPTION_KEY.OPTION_KEY_ID)
            .from(OPTION_KEY)
            .where(OPTION_KEY.NAME.eq(name))
            .and(OPTION_KEY.PRODUCT_ID.eq(productId))
            .fetchOneInto(Long::class.java)

        if (existsId != null) return existsId

        return dsl.insertInto(OPTION_KEY)
            .set(OPTION_KEY.PRODUCT_ID, productId)
            .set(OPTION_KEY.NAME, name)
            .returning(OPTION_KEY.OPTION_KEY_ID)
            .fetchOne(OPTION_KEY.OPTION_KEY_ID)
    }
}