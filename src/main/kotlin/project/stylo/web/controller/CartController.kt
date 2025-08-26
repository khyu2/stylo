package project.stylo.web.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import project.stylo.auth.resolver.Auth
import project.stylo.web.domain.Member
import project.stylo.web.service.CartService

@Controller
@RequestMapping("/cart")
class CartController(
    private val cartService: CartService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CartController::class.java)
    }

    @GetMapping
    fun showCart(@Auth member: Member, model: Model): String {
        val cartItems = cartService.getCartItems(member.memberId!!)
        model.addAttribute("cartItems", cartItems)
        return "cart/index"
    }

    @PostMapping("/add")
    fun addToCart(
        @Auth member: Member,
        @RequestParam productId: Long,
        @RequestParam(required = false) optionId: List<Long>?,
        @RequestParam quantity: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        cartService.addToCart(member.memberId!!, productId, optionId, quantity)
        redirectAttributes.addFlashAttribute("success", "장바구니에 상품이 추가되었습니다.")
        return "redirect:/cart"
    }

    @PostMapping("/update/{cartItemId}")
    fun updateQuantity(
        @Auth member: Member,
        @PathVariable cartItemId: Long,
        @RequestParam quantity: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        cartService.updateQuantity(member.memberId!!, cartItemId, quantity)
        redirectAttributes.addFlashAttribute("success", "수량이 업데이트되었습니다.")
        return "redirect:/cart"
    }

    @PostMapping("/remove/{cartItemId}")
    fun removeFromCart(
        @Auth member: Member,
        @PathVariable cartItemId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        cartService.removeFromCart(member.memberId!!, cartItemId)
        redirectAttributes.addFlashAttribute("success", "상품이 장바구니에서 제거되었습니다.")
        return "redirect:/cart"
    }

    @PostMapping("/clear")
    fun clearCart(
        @Auth member: Member,
        redirectAttributes: RedirectAttributes
    ): String {
        cartService.clearCart(member.memberId!!)
        redirectAttributes.addFlashAttribute("success", "장바구니가 비워졌습니다.")
        return "redirect:/cart"
    }
}
