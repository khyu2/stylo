package project.stylo.web.controller

import jakarta.servlet.http.HttpSession
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import project.stylo.auth.resolver.Auth
import project.stylo.common.aop.LoggedAction
import project.stylo.web.domain.Member
import project.stylo.web.domain.enums.ActionCode
import project.stylo.web.domain.enums.EventType
import project.stylo.web.dto.request.OrderCreateRequest
import project.stylo.web.dto.response.OrderCreateResponse
import project.stylo.web.service.CartService
import project.stylo.web.service.MemberService
import project.stylo.web.service.OrdersService

@Controller
@RequestMapping("/orders")
class OrdersController(
    private val cartService: CartService,
    private val memberService: MemberService,
    private val ordersService: OrdersService,
) {
    @GetMapping
    @LoggedAction(eventType = EventType.VIEW, action = ActionCode.ORDERS_LIST)
    fun getOrders(@Auth member: Member, @PageableDefault(size = 20) pageable: Pageable, model: Model): String {
        val orders = ordersService.getOrdersByMember(member, pageable)
        model.addAttribute("page", orders)
        model.addAttribute("orders", orders.content)
        return "orders/index"
    }

    @GetMapping("/{orderId}")
    @LoggedAction(eventType = EventType.VIEW, action = ActionCode.ORDERS_DETAIL, includeArgs = true)
    fun getOrderDetail(
        @Auth member: Member,
        @PathVariable orderId: Long,
        model: Model
    ): String {
        val detail = ordersService.getOrderDetail(member, orderId)
        model.addAttribute("payment", detail.payment)
        model.addAttribute("order", detail.order)
        model.addAttribute("orderItems", detail.orderItems)
        model.addAttribute("buyer", detail.buyer)
        model.addAttribute("shipping", detail.shipping)
        return "orders/detail"
    }

    @GetMapping("/create")
    @LoggedAction(eventType = EventType.VIEW, action = ActionCode.ORDERS_CREATE_VIEW)
    fun createOrderPage(@Auth member: Member, model: Model): String {
        val cartItems = cartService.getCartItems(member)
        val addresses = memberService.getAddresses(member)
        model.addAttribute("cartItems", cartItems)
        model.addAttribute("addresses", addresses)
        return "orders/create"
    }

    @ResponseBody
    @PostMapping("/create")
    @LoggedAction(eventType = EventType.CREATE, action = ActionCode.ORDERS_CREATE)
    fun createOrder(
        @Auth member: Member,
        @Valid @ModelAttribute request: OrderCreateRequest,
        session: HttpSession,
    ): ResponseEntity<OrderCreateResponse> {
        // 새 주소지 생성
        if (request.addressId == null && request.addressRequest != null)
            memberService.createAddress(member, request.addressRequest).let { request.addressId = it.addressId }

        val response = ordersService.createOrder(member, request, session)
        return ResponseEntity.ok(response)
    }
}
