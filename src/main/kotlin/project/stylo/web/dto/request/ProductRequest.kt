package project.stylo.web.dto.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal

data class ProductRequest(
    @field:NotBlank(message = "상품명은 비어 있을 수 없습니다.")
    @field:Size(max = 100, message = "상품명은 100자를 넘을 수 없습니다.")
    val name: String,

    val description: String?,

    @field:NotNull(message = "가격은 필수입니다.")
    @field:Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    val price: BigDecimal,

    @field:NotNull(message = "재고는 필수입니다.")
    @field:Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    val stock: Long,

    @field:NotNull(message = "카테고리는 필수입니다.")
    val category: Long,

    // 이미지 파일들
    val images: List<MultipartFile> = emptyList(),

    // 옵션 선택
    val genders: List<Long> = emptyList(),
    val sizes: List<Long> = emptyList(),
    val colors: List<Long> = emptyList(),

    // 성별 옵션별 재고/가격
    val genderStocks: Map<Long, Long> = emptyMap(),
    val genderPrices: Map<Long, BigDecimal> = emptyMap(),

    // 사이즈 옵션별 재고/가격
    val sizeStocks: Map<Long, Long> = emptyMap(),
    val sizePrices: Map<Long, BigDecimal> = emptyMap(),

    // 색상 옵션별 재고/가격
    val colorStocks: Map<Long, Long> = emptyMap(),
    val colorPrices: Map<Long, BigDecimal> = emptyMap()
)