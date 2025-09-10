package project.stylo.auth.oauth2

enum class SocialProvider { GOOGLE, KAKAO }

data class SocialMember(
    val provider: SocialProvider,
    val providerId: String,
    val email: String?,
    val name: String?,
    val profileImageUrl: String?
)
