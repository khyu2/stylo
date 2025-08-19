package project.stylo.web.dto.response

import project.stylo.web.domain.Member

data class MemberResponse(
    val memberId: Long,
    val email: String,
    val name: String,
    val role: String,
    val isTerm: Boolean = true,
    val isMarketing: Boolean = false
) {
    companion object {
        fun from(member: Member): MemberResponse {
            return MemberResponse(
                memberId = member.memberId!!,
                email = member.email,
                name = member.name,
                role = member.role.name,
                isTerm = member.isTerm,
                isMarketing = member.isMarketing
            )
        }
    }
}
