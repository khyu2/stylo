package project.stylo.common.utils

import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.Record
import org.jooq.SelectConditionStep
import org.jooq.SelectSeekStepN
import org.jooq.SortField
import org.jooq.Table
import org.jooq.TableRecord
import org.jooq.impl.DSL
import org.springframework.data.domain.Sort

class JooqUtils {
    companion object {
        /**
         * 값이 null이 아니면, 해당 조건을 반환하고, null이면 항상 참인 조건을 반환
         *
         * @param condition 조건 생성 함수
         */
        fun <T> T?.andIfNotNull(condition: (T) -> Condition) =
            this?.let { condition(it) } ?: DSL.noCondition()

        /**
         * 문자열이 null 또는 공백이 아니면, 해당 필드에 대해 대소문자 구분 없는 like 조건을 생성
         *
         * @param field 대상 필드
         */
        fun <T> String?.likeIgnoreCaseIfNotBlank(field: Field<T>) =
            if (!this.isNullOrBlank()) field.likeIgnoreCase("%$this%") else DSL.noCondition()

        /**
         * 정렬 조건을 쿼리에 적용
         *
         * @param sort 정렬 정보
         * @param mapping 정렬 필드 매핑 함수 (예: { property, ascending -> if (ascending) FIELD.asc() else FIELD.desc() })
         * @return 정렬이 적용된 쿼리
         */
        fun <T : Record> SelectConditionStep<T>.applySorting(
            sort: Sort,
            mapping: (property: String, ascending: Boolean) -> SortField<*>
        ): SelectSeekStepN<T> {
            return if (sort.isUnsorted) {
                this.orderBy(emptyList())
            } else {
                val fields = sort.map { order ->
                    mapping(order.property, order.isAscending)
                }.toList()

                this.orderBy(fields)
            }
        }

        /**
         * 리스트가 비어있지 않으면, 해당 조건을 만족하는 레코드가 존재하는지 체크
         *
         * @param dsl DSLContext
         * @param table 대상 테이블
         * @param joinField 테이블에서 비교할 필드 (예: PRODUCT_OPTION.PRODUCT_ID)
         * @param onField 비교할 값 (예: PRODUCT.PRODUCT_ID)
         * @param valueField 비교할 값 컬럼 (예: PRODUCT_OPTION.OPTION_ID)
         */
        fun <T, R : TableRecord<R>> List<T>?.existsIfNotEmpty(
            dsl: DSLContext,
            table: Table<*>,
            joinField: Field<Long?>,   // 외래 키 필드
            onField: Field<Long?>,
            valueField: Field<*>      // 옵션 필드
        ): Condition =
            if (!this.isNullOrEmpty())
                DSL.exists(
                    dsl.selectOne()
                        .from(table)
                        .where(joinField.eq(onField))
                        .and(valueField.`in`(this))
                )
            else DSL.noCondition()
    }
}