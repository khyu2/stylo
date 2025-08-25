package project.stylo.common.utils

import org.jooq.Condition
import org.jooq.Field
import org.jooq.Record
import org.jooq.SelectConditionStep
import org.jooq.SelectSeekStepN
import org.jooq.SortField
import org.jooq.impl.DSL
import org.springframework.data.domain.Sort

class JooqUtils {
    companion object {
        fun <T> T?.andIfNotNull(condition: (T) -> Condition) =
            this?.let { condition(it) } ?: DSL.noCondition()

        fun <T> String?.likeIgnoreCaseIfNotBlank(field: Field<T>) =
            if (!this.isNullOrBlank()) field.likeIgnoreCase("%$this%") else DSL.noCondition()

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

        fun <T> List<T>?.inIfNotEmpty(field: Field<T>) =
            if (!this.isNullOrEmpty()) field.`in`(this) else DSL.noCondition()

        fun <T> String?.containsIfNotBlank(field: Field<T>) =
            if (!this.isNullOrEmpty()) field.like("%$this%") else DSL.noCondition()
    }
}