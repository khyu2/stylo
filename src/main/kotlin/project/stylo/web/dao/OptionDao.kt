package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JOption
import org.jooq.generated.tables.JOptionType
import org.springframework.stereotype.Repository
import project.stylo.web.domain.enums.OptionType
import project.stylo.web.dto.response.OptionResponse

@Repository
class OptionDao(private val dsl: DSLContext) {
    companion object {
        private val OPTION = JOption.OPTION
        private val OPTION_TYPE = JOptionType.OPTION_TYPE
    }

    fun findAllGenderOptions(): List<OptionResponse> =
        dsl.select(OPTION.OPTION_ID, OPTION.VALUE)
            .from(OPTION)
            .join(OPTION_TYPE).on(OPTION_TYPE.OPTION_TYPE_ID.eq(OPTION.OPTION_TYPE_ID))
            .where(OPTION_TYPE.NAME.eq(OptionType.GENDER.name))
            .fetchInto(OptionResponse::class.java)

    fun findAllSizeOptions(): List<OptionResponse> =
        dsl.select(OPTION.OPTION_ID, OPTION.VALUE)
            .from(OPTION)
            .join(OPTION_TYPE).on(OPTION_TYPE.OPTION_TYPE_ID.eq(OPTION.OPTION_TYPE_ID))
            .where(OPTION_TYPE.NAME.eq(OptionType.SIZE.name))
            .fetchInto(OptionResponse::class.java)

    fun findAllColorOptions(): List<OptionResponse> =
        dsl.select(OPTION.OPTION_ID, OPTION.VALUE)
            .from(OPTION)
            .join(OPTION_TYPE).on(OPTION_TYPE.OPTION_TYPE_ID.eq(OPTION.OPTION_TYPE_ID))
            .where(OPTION_TYPE.NAME.eq(OptionType.COLOR.name))
            .fetchInto(OptionResponse::class.java)

}