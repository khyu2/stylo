package project.stylo.web.controller

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
import project.stylo.web.domain.enums.OrderStatus
import project.stylo.web.domain.enums.PaymentStatus
import project.stylo.web.dto.request.OrdersSearchRequest
import project.stylo.web.dto.request.ProductSearchRequest
import project.stylo.web.service.AdminService
import project.stylo.web.service.CategoryService
import project.stylo.web.service.OrdersService
import project.stylo.web.service.ProductService

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val adminService: AdminService,
    private val productService: ProductService,
    private val ordersService: OrdersService,
    private val categoryService: CategoryService,
) {
    @GetMapping
    fun dashboard(model: Model): String {
        val stats = adminService.getDashboardStats()
        model.addAttribute("stats", stats)
        model.addAttribute("activeMenu", "dashboard")
        return "admin/index"
    }

    @GetMapping("/products")
    fun manageProducts(
        @ModelAttribute request: ProductSearchRequest,
        @PageableDefault(size = 20) pageable: Pageable,
        model: Model
    ): String {
        val products = productService.getProducts(request, pageable)
        val categories = categoryService.getAllCategories()

        mapOf(
            "page" to products,
            "products" to products.content,
            "categories" to categories,
            "keyword" to (request.keyword ?: ""),
            "activeMenu" to "products"
        ).forEach { (key, value) ->
            model.addAttribute(key, value)
        }

        return "admin/products/index"
    }

    @GetMapping("/orders")
    fun manageOrders(
        @ModelAttribute request: OrdersSearchRequest,
        @PageableDefault(size = 20) pageable: Pageable,
        model: Model
    ): String {
        val orders = ordersService.getOrders(request, pageable)

        mapOf(
            "page" to orders,
            "orders" to orders.content,
            "statuses" to OrderStatus.entries.toTypedArray(),
            "paymentStatuses" to PaymentStatus.entries.toTypedArray(),
            "keyword" to (request.keyword ?: ""),
            "startDate" to request.startDate,
            "endDate" to request.endDate,
            "orderStatus" to request.orderStatus,
            "paymentStatus" to request.paymentStatus,
            "minPrice" to request.minPrice,
            "maxPrice" to request.maxPrice,
            "activeMenu" to "orders"
        ).forEach { (key, value) ->
            model.addAttribute(key, value)
        }

        return "admin/orders/index"
    }

    @PostMapping("/orders/{orderId}/status")
    fun updateOrderStatus(
        @PathVariable orderId: Long,
        @RequestParam status: OrderStatus
    ): String {
        ordersService.updateStatus(orderId, status)
        return "redirect:/admin/orders"
    }
}
