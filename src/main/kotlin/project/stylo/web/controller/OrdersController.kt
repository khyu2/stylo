package project.stylo.web.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import project.stylo.auth.resolver.Auth
import project.stylo.web.dao.AddressDao
import project.stylo.web.domain.Member
import project.stylo.web.service.CartService

@Controller
@RequestMapping("/orders")
class OrdersController(
    private val cartService: CartService,
    private val addressDao: AddressDao,
) {
    @GetMapping("/create")
    fun createOrderPage(@Auth member: Member, model: Model): String {
        val cartItems = cartService.getCartItems(member)
        val addresses = addressDao.findAllByMemberId(member.memberId!!)
        model.addAttribute("cartItems", cartItems)
        model.addAttribute("addresses", addresses)
        return "orders/create"
    }
}
