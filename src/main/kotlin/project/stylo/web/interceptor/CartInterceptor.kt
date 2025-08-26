package project.stylo.web.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import project.stylo.auth.service.dto.MemberDetails
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
        // 인증된 사용자인 경우에만 장바구니 개수 추가
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated &&
            authentication.principal is MemberDetails
        ) {
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
