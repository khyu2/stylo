package project.stylo.web.dto.response

data class GroupedProductOptionResponse(
    val optionTypeName: String,
    val options: List<ProductOptionResponse>
)
