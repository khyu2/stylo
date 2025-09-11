package project.stylo.web.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import project.stylo.auth.resolver.Auth
import project.stylo.web.domain.Member
import project.stylo.web.service.WishlistService

@Controller
@RequestMapping("/wishlist")
class WishlistController(private val wishlistService: WishlistService) {
    @GetMapping
    fun index(@Auth member: Member, model: Model): String {
        val items = wishlistService.getItems(member)
        model.addAttribute("items", items)
        return "wishlist/index"
    }

    @PostMapping("/add")
    fun add(
        @Auth member: Member,
        @RequestParam productId: Long,
        request: HttpServletRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        wishlistService.add(member, productId)
        redirectAttributes.addFlashAttribute("success", "위시리스트에 추가되었습니다.")
        return request.getHeader("Referer")?.let { "redirect:$it" } ?: "redirect:/wishlist"
    }

    @PostMapping("/remove")
    fun remove(
        @Auth member: Member,
        @RequestParam productId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        wishlistService.remove(member, productId)
        redirectAttributes.addFlashAttribute("success", "위시리스트에서 제거되었습니다.")
        return "redirect:/wishlist"
    }

    @PostMapping("/toggle")
    fun toggle(
        @Auth member: Member,
        @RequestParam productId: Long,
        request: HttpServletRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        val added = wishlistService.toggle(member, productId)
        redirectAttributes.addFlashAttribute(
            if (added) "success" else "info",
            if (added) "위시리스트에 추가되었습니다." else "위시리스트에서 제거되었습니다."
        )
        return request.getHeader("Referer")?.let { "redirect:$it" } ?: "redirect:/wishlist"
    }
}
