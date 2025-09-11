package project.stylo.web.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.cache.annotation.CacheEvict
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.common.config.CacheConfig.Companion.CATEGORY_CACHE
import project.stylo.common.exception.BaseException
import project.stylo.common.s3.FileStorageService
import project.stylo.web.dao.ImageDao
import project.stylo.web.dao.OptionKeyDao
import project.stylo.web.dao.OptionValueDao
import project.stylo.web.dao.OptionVariantDao
import project.stylo.web.dao.ProductDao
import project.stylo.web.dao.ProductOptionDao
import project.stylo.web.domain.Member
import project.stylo.web.domain.Product
import project.stylo.web.domain.enums.ImageOwnerType
import project.stylo.web.dto.request.ProductRequest
import project.stylo.web.dto.request.ProductSearchRequest
import project.stylo.web.dto.response.OptionDefinitionResponse
import project.stylo.web.dto.response.ProductOptionResponse
import project.stylo.web.dto.response.ProductResponse
import project.stylo.web.exception.ProductExceptionType

@Service
@Transactional
class ProductService(
    private val imageDao: ImageDao,
    private val productDao: ProductDao,
    private val optionKeyDao: OptionKeyDao,
    private val optionValueDao: OptionValueDao,
    private val optionVariantDao: OptionVariantDao,
    private val productOptionDao: ProductOptionDao,
    private val fileStorageService: FileStorageService
) {
    @CacheEvict(CATEGORY_CACHE, allEntries = true)
    fun createProduct(member: Member, request: ProductRequest): ProductResponse {
        if (productDao.existsByName(request.name)) {
            throw BaseException(ProductExceptionType.PRODUCT_DUPLICATED)
        }

        // 상품 생성
        val product = productDao.save(member.memberId!!, request)

        // 옵션 및 옵션 값, 옵션 조합 생성
        if (request.combinations.isEmpty()) throw BaseException(ProductExceptionType.NO_COMBINATION_PROVIDED)

        request.combinations.forEach { combination ->
            val productOptionId = productOptionDao.save(combination, product.productId)

            combination.options.forEach { option ->
                val name = option["name"] ?: throw BaseException(ProductExceptionType.OPTION_NAME_MISSING)
                val value = option["value"] ?: throw BaseException(ProductExceptionType.OPTION_VALUE_MISSING)

                optionKeyDao.saveOrGetId(product.productId, name)?.let { optionKeyId ->
                    optionValueDao.saveOrGetId(optionKeyId, value)?.let { optionValueId ->
                        optionVariantDao.save(productOptionId, optionValueId)
                    }
                }
            }
        }

        if (request.images.isEmpty()) throw BaseException(ProductExceptionType.NO_IMAGE_PROVIDED)

        // 이미지 업로드 처리
        request.images.forEachIndexed { index, file ->
            if (!file.isEmpty) {
                val uploadUrl = fileStorageService.upload(file, ImageOwnerType.PRODUCT, product.productId)
                imageDao.save(product.productId, ImageOwnerType.PRODUCT, uploadUrl)

                if (index == 0) {
                    productDao.updateThumbnail(product.productId, uploadUrl)
                }
            }
        }
        val productImages = getProductImages(product.productId)

        return ProductResponse.from(product, productImages)
    }

    @Transactional(readOnly = true)
    fun getProduct(productId: Long): ProductResponse {
        val product = productDao.findById(productId) ?: throw BaseException(ProductExceptionType.PRODUCT_NOT_FOUND)

        val options = productOptionDao.findAllByProductId(productId).map { productOption ->
            val optionValues = optionValueDao.findAllByProductOptionId(productOption.productOptionId)
            ProductOptionResponse.from(productOption, optionValues.joinToString(", "))
        }

        val optionDefinitions = optionKeyDao.findAllByProductId(productId).map { keys ->
            val values = optionValueDao.findAllByOptionKeyId(keys.optionKeyId)
            OptionDefinitionResponse(
                name = keys.name, values = values
            )
        }

        val productImages = getProductImages(productId)

        return ProductResponse.from(product, productImages).copy(
            options = options, optionDefinitions = optionDefinitions
        )
    }

    @Transactional(readOnly = true)
    fun getProductImages(productId: Long): List<String> {
        val imageUrls = imageDao.findAllByProductId(productId)
        return getAllPresignedUrl(imageUrls)
    }

    @Transactional(readOnly = true)
    fun getProductImagePaths(productId: Long): List<String> {
        return imageDao.findAllByProductId(productId)
    }

    @Transactional(readOnly = true)
    fun getProducts(request: ProductSearchRequest, pageable: Pageable): Page<ProductResponse> {
        val products = productDao.findAll(request, pageable)

        // 상품 ID 별 이미지 일괄 조회
        val productIds = products.content.map { it.productId }
        val productImagesMap = imageDao.findAllByProductIds(productIds)

        // 모든 이미지 URL을 한 번에 수집
        val allImageUrls = products.content.flatMap { product ->
            productImagesMap[product.productId] ?: emptyList()
        }

        // 모든 이미지를 한 번에 병렬로 presign
        val allPresignedUrls = getAllPresignedUrl(allImageUrls)

        // presigned URL을 다시 상품별로 매핑
        var urlIndex = 0
        val productResponses = products.content.map { product ->
            val productImages = productImagesMap[product.productId] ?: emptyList()
            val presignedImages = allPresignedUrls.subList(urlIndex, urlIndex + productImages.size)
            urlIndex += productImages.size
            ProductResponse.from(product, presignedImages)
        }

        return PageImpl(productResponses, pageable, products.totalElements)
    }

    fun updateProduct(productId: Long, request: ProductRequest) {
        val product = productDao.findById(productId) ?: throw BaseException(ProductExceptionType.PRODUCT_NOT_FOUND)

        updateProductImages(request, product)

        val updatedProduct = product.copy(
            name = request.name,
            description = request.description,
            price = request.price,
            categoryId = request.categoryId,
        )

        productDao.update(updatedProduct)
    }

    fun deleteProduct(productId: Long) {
        val product = productDao.findById(productId)
        product?.let {
            val imageUrls = imageDao.findAllByProductId(productId)
            imageUrls.forEach { imageUrl ->
                fileStorageService.delete(imageUrl)
            }
        }

        productDao.delete(productId)
    }

    /**
     * 이미지 순서 적용 업데이트
     *
     * 기존 이미지 중 재사용되는 것들은 스토리지에서 삭제하지 않고 유지하며, DB 레코드만 초기화 후 imageOrder에 따라 재생성한다.
     */
    private fun updateProductImages(request: ProductRequest, product: Product) {
        // 1) imageOrder 토큰 파싱
        val orderTokens: List<String> = try {
            val mapper = jacksonObjectMapper()
            request.imageOrder?.let {
                mapper.readValue(
                    it, mapper.typeFactory.constructCollectionType(List::class.java, String::class.java)
                )
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        // 2) 유지해야 할 기존 경로 집합 계산
        val keptExistingPaths: Set<String> =
            orderTokens.asSequence().filter { it.startsWith("existing:") }.map { it.removePrefix("existing:").trim() }
                .filter { it.isNotBlank() }.toSet()

        // 3) 현재 저장된 모든 이미지 경로 조회 후, 유지 대상이 아닌 파일만 스토리지에서 삭제
        val currentPaths = imageDao.findAllByProductId(product.productId)
        currentPaths.forEach { path ->
            if (!keptExistingPaths.contains(path)) {
                fileStorageService.delete(path)
            }
        }
        // 4) 이미지 테이블에서 해당 상품의 모든 레코드 삭제 (이후 imageOrder 순서대로 재삽입)
        imageDao.deleteAllByOwnerIdAndOwnerType(product.productId, ImageOwnerType.PRODUCT)

        // 5) imageOrder 순서대로 재구성 (existing은 경로를 재등록, new는 업로드)
        var thumbnailSet = false
        var newFileIndex = 0
        orderTokens.forEach { token ->
            when {
                token.startsWith("existing:") -> {
                    val path = token.removePrefix("existing:").trim()
                    if (path.isNotBlank()) {
                        imageDao.save(product.productId, ImageOwnerType.PRODUCT, path)
                        if (!thumbnailSet) {
                            productDao.updateThumbnail(product.productId, path)
                            thumbnailSet = true
                        }
                    }
                }

                token == "new" -> {
                    if (newFileIndex < request.images.size) {
                        val file = request.images[newFileIndex++]
                        if (!file.isEmpty) {
                            val uploadUrl = fileStorageService.upload(file, ImageOwnerType.PRODUCT, product.productId)
                            imageDao.save(product.productId, ImageOwnerType.PRODUCT, uploadUrl)
                            if (!thumbnailSet) {
                                productDao.updateThumbnail(product.productId, uploadUrl)
                                thumbnailSet = true
                            }
                        }
                    }
                }
            }
        }
    }

    fun getAllPresignedUrl(urls: List<String>): List<String> =
        runBlocking {
            urls.map { async(Dispatchers.IO) { fileStorageService.getPresignedUrl(it) } }.awaitAll()
        }
}