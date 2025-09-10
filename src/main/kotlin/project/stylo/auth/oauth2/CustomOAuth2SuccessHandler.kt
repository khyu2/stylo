package project.stylo.auth.oauth2

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import project.stylo.auth.service.dto.MemberDetails

@Component
class CustomOAuth2SuccessHandler(
    private val socialMemberService: SocialMemberService,
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val registrationId = request.getParameter("state_registration_id")
            ?: authentication.javaClass.getDeclaredFieldOrNull("authorizedClientRegistrationId")?.let { field ->
                field.isAccessible = true
                field.get(authentication) as? String
            }
            ?: request.getAttribute("org.springframework.security.oauth2.client.registration_id") as? String
            ?: // fallback from request URI /login/oauth2/code/{registrationId}
            request.requestURI.substringAfterLast("/")

        val info = SocialMemberExtractor.extract(registrationId, authentication)
        val member = socialMemberService.findOrCreate(info)

        val principal = MemberDetails(member, info.profileImageUrl)
        val authToken = UsernamePasswordAuthenticationToken(
            principal,
            null,
            principal.authorities
        )

        SecurityContextHolder.getContext().authentication = authToken
        request.session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext())

        val phone = principal.member.phone
        if (phone.isNullOrBlank()) {
            response.sendRedirect("/auth/contact")
            return
        }

        response.sendRedirect("/")
    }
}

private fun Any.getDeclaredFieldOrNull(name: String) = try {
    this::class.java.getDeclaredField(name)
} catch (e: NoSuchFieldException) {
    null
}
