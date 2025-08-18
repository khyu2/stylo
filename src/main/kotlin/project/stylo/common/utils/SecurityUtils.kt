package project.stylo.common.utils

import org.springframework.security.core.context.SecurityContextHolder
import kotlin.takeIf

class SecurityUtils {

    companion object {
        fun isAuthenticated(): Boolean =
            SecurityContextHolder.getContext().authentication.isAuthenticated &&
                    SecurityContextHolder.getContext().authentication.name != "anonymousUser"

        fun getUsername(): String? =
            SecurityContextHolder.getContext().authentication.name.takeIf { isAuthenticated() }
    }
}