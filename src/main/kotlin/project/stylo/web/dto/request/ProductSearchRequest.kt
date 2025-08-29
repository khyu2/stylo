package project.stylo.web.dto.request

import java.math.BigDecimal

data class ProductSearchRequest(
    val categoryId: Long? = null,
    val keyword: String? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val genderIds: List<String> = emptyList(),
    val sizeIds: List<String> = emptyList(),
    val colorIds: List<String> = emptyList(),
    val sortBy: String = "createdAt",
    val sortDirection: String = "DESC"
)
