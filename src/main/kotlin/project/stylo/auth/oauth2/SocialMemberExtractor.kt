package project.stylo.auth.oauth2

import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import project.stylo.auth.exception.AuthExceptionType
import project.stylo.common.exception.BaseException

object SocialMemberExtractor {
    private val logger = LoggerFactory.getLogger(SocialMemberExtractor::class.java)

    fun extract(registrationId: String, auth: Authentication): SocialMember {
        val provider = when (registrationId.lowercase()) {
            "google" -> SocialProvider.GOOGLE
            "kakao" -> SocialProvider.KAKAO
            else -> throw BaseException(AuthExceptionType.UNSUPPORTED_PROVIDER)
        }

        val principal = auth.principal as OAuth2User

        logger.info("OAuth2User attributes: ${principal.attributes}")

        return when (provider) {
            SocialProvider.GOOGLE -> extractGoogle(principal)
            SocialProvider.KAKAO -> extractKakao(principal)
        }
    }

    private fun extractGoogle(user: OAuth2User): SocialMember {
        val providerId = user.getAttribute<String>("sub") ?: ""
        val email = user.getAttribute<String>("email")
        val name = user.getAttribute<String>("name") ?: email ?: "Google User"
        val picture = user.getAttribute<String>("picture")
        return SocialMember(SocialProvider.GOOGLE, providerId, email, name, picture)
    }

    private fun extractKakao(user: OAuth2User): SocialMember {
        val providerId = (user.getAttribute<Any>("id")?.toString()) ?: ""
        val kakaoAccount = user.getAttribute<Map<String, Any>>("kakao_account")
        val email = kakaoAccount?.get("email") as? String
        val profile = kakaoAccount?.get("profile") as? Map<*, *>
        val nickname = profile?.get("nickname") as? String
        val profileImage = profile?.get("profile_image_url") as? String
        val name = nickname ?: email ?: "Kakao User"
        return SocialMember(SocialProvider.KAKAO, providerId, email, name, profileImage)
    }
}
