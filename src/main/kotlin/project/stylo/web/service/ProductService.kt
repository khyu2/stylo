package project.stylo.web.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
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
import project.stylo.web.dto.response.PresignedUrlResponse
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
        if (request.combinations.isEmpty())
            throw BaseException(ProductExceptionType.NO_COMBINATION_PROVIDED)

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

        if (request.images.isEmpty())
            throw BaseException(ProductExceptionType.NO_IMAGE_PROVIDED)

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
                name = keys.name,
                values = values
            )
        }

        val productImages = getProductImages(productId)

        return ProductResponse.from(product, productImages).copy(
            options = options,
            optionDefinitions = optionDefinitions
        )
    }

    @Transactional(readOnly = true)
    fun getProductImages(productId: Long): List<PresignedUrlResponse> {
        val imageUrls = imageDao.findAllByProductId(productId)
        return imageUrls.map { imageUrl ->
            val presignedUrl = fileStorageService.getPresignedUrl(imageUrl)
            PresignedUrlResponse.from(presignedUrl)
        }
    }

    @Transactional(readOnly = true)
    fun getProducts(request: ProductSearchRequest, pageable: Pageable): Page<ProductResponse> {
        val productPage = productDao.searchProducts(request, pageable)

        val productResponses = productPage.content.map { product ->
            val productImages = getProductImages(product.productId)
            ProductResponse.from(product, productImages)
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

    private fun uploadImages(product: Product, images: List<MultipartFile>) {

    }
}
