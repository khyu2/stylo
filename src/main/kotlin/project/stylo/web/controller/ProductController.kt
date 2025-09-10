package project.stylo.web.controller

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import project.stylo.auth.resolver.Auth
import project.stylo.web.domain.Member
import project.stylo.web.dto.request.ProductRequest
import project.stylo.web.service.CategoryService
import project.stylo.web.service.ProductService

@Controller
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
    private val categoryService: CategoryService,
) {
    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    fun showCreateForm(model: Model): String {
        model.addAttribute("categories", categoryService.getAllCategories())
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

        model.addAttribute("product", product)
        return "product/detail"
    }

    @GetMapping("/{productId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    fun showEditForm(@PathVariable productId: Long, model: Model): String {
        val product = productService.getProduct(productId)
        model.addAttribute("product", product)
        model.addAttribute("categories", categoryService.getAllCategories())
        model.addAttribute("productImages", product.productImages)
        model.addAttribute("productImagePaths", productService.getProductImagePaths(productId))
        return "product/edit"
    }

    @PostMapping("/{productId}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateProduct(
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