package project.stylo.web.dto.response

data class GenderOptionResponse(
    val genderId: Long,
    val name: String,
)

data class SizeOptionResponse(
    val sizeId: Long,
    val name: String,
)

data class ColorOptionResponse(
    val colorId: Long,
    val name: String,
    val hexCode: String,
)
