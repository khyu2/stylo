package project.stylo.auth.resolver

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import project.stylo.auth.service.dto.MemberDetails
import project.stylo.common.exception.BaseException
import project.stylo.common.exception.BaseExceptionType
import project.stylo.common.utils.SecurityUtils

@Component
class AuthArgumentResolver : HandlerMethodArgumentResolver {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthArgumentResolver::class.java)
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Auth::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication

        // 인증되지 않은 사용자
        if (authentication == null || !SecurityUtils.isAuthenticated()) {
            val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            val ip = extractClientIp(request)
            logger.info("익명 사용자 요청 IP = $ip")
            return ip
        }

        val principal = authentication.principal
        if (principal !is MemberDetails) {
            logger.error("인증된 사용자 정보가 MemberDetails 타입이 아닙니다: $principal")
            throw BaseException(BaseExceptionType.UNAUTHORIZED)
        }

        return principal.member
    }

    private fun extractClientIp(request: HttpServletRequest?): String {
        if (request == null) return "UNKNOWN"
        val ip = request.getHeader("X-Forwarded-For")
        return if (ip.isNullOrBlank()) request.remoteAddr else ip
    }
}