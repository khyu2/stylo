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
        val categories = categoryService.getAllCategories()
        val genderOptions = categoryService.getAllGenderOptions()
        val sizeOptions = categoryService.getAllSizeOptions()
        val colorOptions = categoryService.getAllColorOptions()

        model.addAttribute("categories", categories)
        model.addAttribute("genderOptions", genderOptions)
        model.addAttribute("sizeOptions", sizeOptions)
        model.addAttribute("colorOptions", colorOptions)

        return "index"
    }
}