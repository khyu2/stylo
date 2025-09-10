package project.stylo.web.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import project.stylo.auth.service.dto.MemberDetails
import project.stylo.auth.utils.SecurityUtils
import project.stylo.web.service.CartService

@Component
class CartInterceptor(
    private val cartService: CartService
) : HandlerInterceptor {

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        if (SecurityUtils.isAuthenticated()) {
            val authentication = SecurityContextHolder.getContext().authentication
            val memberDetails = authentication.principal as MemberDetails
            val memberId = memberDetails.member.memberId

            if (memberId != null) {
                val cartItemCount = cartService.getCartItemCount(memberId)
                modelAndView?.addObject("cartItemCount", cartItemCount)
            }
        } else {
            modelAndView?.addObject("cartItemCount", 0)
        }
    }
}
