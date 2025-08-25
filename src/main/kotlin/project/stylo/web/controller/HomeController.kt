package project.stylo.web.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import project.stylo.web.service.CategoryService

@Controller
class HomeController(
    private val categoryService: CategoryService
) {
    @GetMapping
    fun home(model: Model): String {
        mapOf(
            "categories" to categoryService.getAllCategories(),
            "genderOptions" to categoryService.getAllGenderOptions(),
            "sizeOptions" to categoryService.getAllSizeOptions(),
            "colorOptions" to categoryService.getAllColorOptions()
        ).forEach { (key, value) ->
            model.addAttribute(key, value)
        }

        return "index"
    }
}