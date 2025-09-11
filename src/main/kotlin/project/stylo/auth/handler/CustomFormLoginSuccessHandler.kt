package project.stylo.auth.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import project.stylo.auth.service.dto.MemberDetails
import project.stylo.web.service.CartService

@Component
class CustomFormLoginSuccessHandler(
    private val cartService: CartService
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // 사용자 정보 로깅 또는 추가 처리
        val username = authentication.name
        logger.info("사용자 로그인 성공: $username")

        // 장바구니 수량 업데이트
        val principal = authentication.principal
        val memberDetails = principal as MemberDetails
        val member = memberDetails.member
        val cartCount = cartService.getCartItemCount(member.memberId!!)
        memberDetails.cartCount = cartCount

        // 기본 성공 URL로 리다이렉트
        defaultTargetUrl = "/"
        super.onAuthenticationSuccess(request, response, authentication)
    }
}