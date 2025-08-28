package project.stylo.web.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import project.stylo.common.config.CacheConfig.Companion.PRODUCT_CACHE
import project.stylo.common.exception.BaseException
import project.stylo.common.s3.FileStorageService
import project.stylo.web.dao.ImageDao
import project.stylo.web.dao.ProductDao
import project.stylo.web.domain.Member
import project.stylo.web.domain.Product
import project.stylo.web.domain.enums.ImageOwnerType
import project.stylo.web.dto.request.ProductRequest
import project.stylo.web.dto.request.ProductSearchRequest
import project.stylo.web.dto.response.PresignedUrlResponse
import project.stylo.web.dto.response.ProductOptionResponse
import project.stylo.web.dto.response.ProductResponse
import project.stylo.web.exception.ProductExceptionType
import java.math.BigDecimal

@Service
@Transactional
class ProductService(
    private val imageDao: ImageDao,
    private val productDao: ProductDao,
    private val fileStorageService: FileStorageService
) {
    fun createProduct(member: Member, request: ProductRequest): ProductResponse {
        if (productDao.existsByName(request.name)) {
            throw BaseException(ProductExceptionType.PRODUCT_DUPLICATED)
        }

        // thumbnailUrl 설정 (첫 번째 이미지 사용)
        val thumbnailUrl = request.images.firstOrNull { !it.isEmpty }?.let { file ->
            fileStorageService.upload(file, ImageOwnerType.PRODUCT)
        }

        // 상품 생성
        val product = productDao.save(member.memberId!!, request)

        productDao.updateThumbnail(product.productId, thumbnailUrl)

        // 이미지 업로드 처리 (첫 번째 이미지는 썸네일로 사용했으므로 제외)
        uploadImages(product, request.images.drop(1))

        val productUrl = fileStorageService.getPresignedUrl(thumbnailUrl!!)

        // 옵션별 재고/가격 정보 처리
        processProductOptions(product.productId, request)

        return ProductResponse.from(product, productUrl)
    }

    @Transactional(readOnly = true)
    fun getProduct(productId: Long): ProductResponse =
        productDao.findById(productId)
            ?.let {
                val productUrl = fileStorageService.getPresignedUrl(it.thumbnailUrl!!)
                ProductResponse.from(it, productUrl)
            }
            ?: throw BaseException(ProductExceptionType.PRODUCT_NOT_FOUND)

    @Cacheable(PRODUCT_CACHE, key = "#productId")
    @Transactional(readOnly = true)
    fun getProductImages(productId: Long): List<PresignedUrlResponse> {
        val imageUrls = imageDao.findAllByProductId(productId)
        return imageUrls.map { imageUrl ->
            val presignedUrl = fileStorageService.getPresignedUrl(imageUrl)
            PresignedUrlResponse.from(presignedUrl)
        }
    }

//    @Transactional(readOnly = true)
//    fun getProductOptions(productId: Long): List<ProductOptionResponse> =
//        productOptionDao.findProductOptionsWithDetails(productId)

    @Transactional(readOnly = true)
    fun getProducts(request: ProductSearchRequest, pageable: Pageable): Page<ProductResponse> {
        val productPage = productDao.searchProducts(request, pageable)

        val productResponses = productPage.content.map { product ->
            val productUrl = product.thumbnailUrl?.let { fileStorageService.getPresignedUrl(it) }
            ProductResponse.from(product, productUrl ?: "")
        }

        return PageImpl(productResponses, pageable, productPage.totalElements)
    }

    fun updateProduct(productId: Long, request: ProductRequest) {
        val product = productDao.findById(productId)
            ?: throw BaseException(ProductExceptionType.PRODUCT_NOT_FOUND)

        // 이미지 업로드 처리 (기존 이미지 삭제 후 새 이미지 업로드)
        uploadImages(product, request.images)

        val updatedProduct = product.copy(
            name = request.name,
            description = request.description,
            price = request.price,
            categoryId = request.category,
        )

        productDao.update(updatedProduct)

        // 옵션 정보 업데이트
        updateProductOptions(productId, request)
    }

    fun deleteProduct(productId: Long) {
        // 상품 관련 이미지 삭제
        val product = productDao.findById(productId)
        product?.let {
            // 이미지 URL에서 파일명 추출하여 삭제
            // 실제 구현에서는 이미지 URL을 저장하는 별도 테이블이 필요할 수 있음
        }

        productDao.delete(productId)
    }

    private fun uploadImages(product: Product, images: List<MultipartFile>) {
        images.map { file ->
            if (!file.isEmpty) {
                val uploadUrl = fileStorageService.upload(file, ImageOwnerType.PRODUCT, product.productId)
                imageDao.save(product.productId, ImageOwnerType.PRODUCT, uploadUrl)
            }
        }
    }

    private fun processProductOptions(productId: Long, request: ProductRequest) {
        // 성별 옵션 처리
        request.genders.forEach { genderId ->
            val stock = request.genderStocks[genderId] ?: 0L
            val price = request.genderPrices[genderId] ?: BigDecimal.ZERO
//            productOptionDao.save(productId, genderId, price, stock)
        }

        // 사이즈 옵션 처리
        request.sizes.forEach { sizeId ->
            val stock = request.sizeStocks[sizeId] ?: 0L
            val price = request.sizePrices[sizeId] ?: BigDecimal.ZERO
//            productOptionDao.save(productId, sizeId, price, stock)
        }

        // 색상 옵션 처리
        request.colors.forEach { colorId ->
            val stock = request.colorStocks[colorId] ?: 0L
            val price = request.colorPrices[colorId] ?: BigDecimal.ZERO
//            productOptionDao.save(productId, colorId, price, stock)
        }
    }

    private fun updateProductOptions(productId: Long, request: ProductRequest) {
        // 기존 옵션 삭제
//        productOptionDao.deleteByProductId(productId)

        // 새로운 옵션 정보 저장
        processProductOptions(productId, request)
    }
}
