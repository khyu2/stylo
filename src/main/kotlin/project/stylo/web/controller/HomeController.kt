package project.stylo.web.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import project.stylo.web.dto.request.ProductSearchRequest
import project.stylo.web.service.CategoryService
import project.stylo.web.service.ProductOptionService
import project.stylo.web.service.ProductService

@Controller
class HomeController(
    private val productService: ProductService,
    private val categoryService: CategoryService,
    private val productOptionService: ProductOptionService
) {
    @GetMapping
    fun home(
        model: Model,
        @PageableDefault(size = 12) pageable: Pageable,
        @ModelAttribute("searchRequest") request: ProductSearchRequest
    ): String {
        val products = productService.getProducts(request, pageable)

        mapOf(
            "categories" to categoryService.getAllCategories(),
            "genderOptions" to productOptionService.getGenderOptions(),
            "sizeOptions" to productOptionService.getSizeOptions(),
            "colorOptions" to productOptionService.getColorOptions(),
            "products" to products,
            "currentPage" to products.number,
            "size" to products.size,
            "searchRequest" to request,
            "currentCategoryId" to request.categoryId,
            "pageable" to pageable
        ).forEach { (key, value) ->
            model.addAttribute(key, value)
        }

        return "index"
    }
}