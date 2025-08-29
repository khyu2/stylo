package project.stylo.web.dto.request

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile
import project.stylo.common.utils.CommaSeparatedStringDeserializer
import project.stylo.common.utils.OptionMapListDeserializer
import java.math.BigDecimal

data class ProductRequest(
    @field:NotBlank(message = "상품명은 비어 있을 수 없습니다.")
    @field:Size(max = 100, message = "상품명은 100자를 넘을 수 없습니다.")
    val name: String,

    val description: String?,

    @field:NotNull(message = "가격은 필수입니다.")
    @field:Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    val price: BigDecimal,

    @field:NotNull(message = "카테고리는 필수입니다.")
    val categoryId: Long,

    // 이미지 파일들
    val images: List<MultipartFile> = emptyList(),

    // 옵션 정의 (새로운 구조)
    val options: List<OptionDefinition> = emptyList(),

    // 옵션 조합 (SKU 기반)
    val combinations: List<OptionCombination> = emptyList()
)

data class OptionDefinition(
    val name: String,  // GENDER, SIZE, COLOR
    @JsonDeserialize(using = CommaSeparatedStringDeserializer::class)
    val values: List<String> // 쉼표로 구분된 값들 (예: "남성,여성")
)

data class OptionCombination(
    val sku: String,
    val additionalPrice: BigDecimal,
    val stock: Long,
    val options: List<Map<String, String>>
)