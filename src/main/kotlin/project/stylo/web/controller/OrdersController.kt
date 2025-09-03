package project.stylo.web.controller

import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import project.stylo.auth.resolver.Auth
import project.stylo.web.domain.Member
import project.stylo.web.dto.request.OrderCreateRequest
import project.stylo.web.service.CartService
import project.stylo.web.service.MemberService

@Controller
@RequestMapping("/orders")
class OrdersController(
    private val cartService: CartService,
    private val memberService: MemberService
) {
    @GetMapping("/create")
    fun createOrderPage(@Auth member: Member, model: Model): String {
        val cartItems = cartService.getCartItems(member)
        val addresses = memberService.getAddresses(member)
        model.addAttribute("cartItems", cartItems)
        model.addAttribute("addresses", addresses)
        return "orders/create"
    }

    @PostMapping("/create")
    fun createOrder(
        @Auth member: Member,
        @Valid @ModelAttribute request: OrderCreateRequest
    ): String {
        return "redirect:/orders"
    }
}
