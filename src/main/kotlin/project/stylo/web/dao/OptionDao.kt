package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JColorOption
import org.jooq.generated.tables.JGenderOption
import org.jooq.generated.tables.JSizeOption
import org.springframework.stereotype.Repository
import project.stylo.web.dto.response.ColorOptionResponse
import project.stylo.web.dto.response.GenderOptionResponse
import project.stylo.web.dto.response.SizeOptionResponse

/**
 * 3 가지 옵션 테이블을 관리하는 DAO 클래스
 * 각 옵션은 성별, 사이즈, 색상에 대한 정보를 담고 있습니다.
 */
@Repository
class OptionDao(private val dsl: DSLContext) {
    companion object {
        private val GENDER_OPTION = JGenderOption.GENDER_OPTION
        private val SIZE_OPTION = JSizeOption.SIZE_OPTION
        private val COLOR_OPTION = JColorOption.COLOR_OPTION
    }

    fun findAllGenderOptions(): List<GenderOptionResponse> =
        dsl.selectFrom(GENDER_OPTION).fetchInto(GenderOptionResponse::class.java)

    fun findAllSizeOptions(): List<SizeOptionResponse> =
        dsl.selectFrom(SIZE_OPTION).fetchInto(SizeOptionResponse::class.java)

    fun findAllColorOptions(): List<ColorOptionResponse> =
        dsl.selectFrom(COLOR_OPTION).fetchInto(ColorOptionResponse::class.java)
}