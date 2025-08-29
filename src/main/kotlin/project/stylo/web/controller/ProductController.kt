package project.stylo.web.controller

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import project.stylo.auth.resolver.Auth
import project.stylo.web.domain.Member
import project.stylo.web.dto.request.ProductRequest
import project.stylo.web.dto.request.ProductSearchRequest
import project.stylo.web.dto.response.ProductListResponse
import project.stylo.web.dto.response.ProductResponse
import project.stylo.web.dto.response.GroupedProductOptionResponse
import project.stylo.web.service.CategoryService
import project.stylo.web.service.ProductService

@Controller
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
    private val categoryService: CategoryService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ProductController::class.java)
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    fun showCreateForm(model: Model): String {
        mapOf(
            "categories" to categoryService.getAllCategories(),
//            "genderOptions" to categoryService.getAllGenderOptions(),
//            "sizeOptions" to categoryService.getAllSizeOptions(),
//            "colorOptions" to categoryService.getAllColorOptions()
        ).forEach { (key, value) ->
            model.addAttribute(key, value)
        }

        return "product/create"
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    fun createProduct(
        @Auth member: Member,
        @Valid @ModelAttribute request: ProductRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        val productResponse = productService.createProduct(member, request)
        redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 등록되었습니다.")
        return "redirect:/products/${productResponse.productId}"
    }

    @GetMapping("/{productId}")
    fun showProduct(@PathVariable productId: Long, model: Model): String {
        val product = productService.getProduct(productId)
        val productImages = productService.getProductImages(productId)

        logger.info("product option details: ${product.options}")

        model.addAttribute("product", product)
        model.addAttribute("productImages", productImages)
        return "product/detail"
    }

    @GetMapping("/{productId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    fun showEditForm(@PathVariable productId: Long, model: Model): String {
        val product = productService.getProduct(productId)
        model.addAttribute("product", product)
        return "product/edit"
    }

    @PostMapping("/{productId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateProduct(
        @Auth member: Member,
        @Valid @ModelAttribute request: ProductRequest,
        @PathVariable productId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        productService.updateProduct(productId, request)
        redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 수정되었습니다.")
        return "redirect:/products/$productId"
    }

    @PostMapping("/{productId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteProduct(
        @PathVariable productId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        productService.deleteProduct(productId)
        redirectAttributes.addFlashAttribute("success", "상품이 성공적으로 삭제되었습니다.")
        return "redirect:/"
    }
}

@RestController
@RequestMapping("/api/products")
class ProductApiController(
    private val productService: ProductService
) {
    @GetMapping
    fun getProducts(
        @PageableDefault pageable: Pageable,
        @ModelAttribute("request") request: ProductSearchRequest
    ): Page<ProductResponse> {
        return productService.getProducts(request, pageable)
    }
}
