package project.stylo.web.dto.request

data class MemberUpdateRequest(
    val name: String? = null,
    val password: String? = null,
    val isMarketing: Boolean? = null
)
