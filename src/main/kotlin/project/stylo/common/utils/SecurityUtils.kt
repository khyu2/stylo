package project.stylo.common.utils

import org.springframework.security.core.context.SecurityContextHolder
import project.stylo.auth.service.dto.MemberDetails
import project.stylo.web.domain.Member

class SecurityUtils {
    companion object {
        fun isAuthenticated(): Boolean =
            SecurityContextHolder.getContext().authentication.isAuthenticated &&
                    SecurityContextHolder.getContext().authentication.name != "anonymousUser"

        fun getUsername(): String? =
            SecurityContextHolder.getContext().authentication.name.takeIf { isAuthenticated() }

        fun updateProfile(member: Member) {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication.principal is MemberDetails) {
                val principal = authentication.principal as MemberDetails
                principal.member = member
            }
        }

        fun updateProfileUrl(profileUrl: String?) {
            val authentication = SecurityContextHolder.getContext().authentication
            if (authentication.principal is MemberDetails) {
                val principal = authentication.principal as MemberDetails
                principal.profileUrl = profileUrl
            }
        }
    }
}