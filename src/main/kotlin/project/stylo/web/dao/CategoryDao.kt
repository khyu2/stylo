package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JCategory
import org.springframework.stereotype.Repository
import project.stylo.web.dto.response.CategoryResponse

@Repository
class CategoryDao(private val dsl: DSLContext) {
    companion object {
        private val CATEGORY = JCategory.CATEGORY
    }

    fun findAll(): List<CategoryResponse> =
        dsl.select(CATEGORY.CATEGORY_ID, CATEGORY.NAME)
            .from(CATEGORY)
            .fetchInto(CategoryResponse::class.java)

}