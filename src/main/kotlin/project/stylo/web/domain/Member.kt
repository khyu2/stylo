package project.stylo.web.domain

import project.stylo.web.domain.enums.MemberRole
import java.time.LocalDateTime

data class Member(
    val memberId: Long?,
    val email: String,
    val password: String,
    val name: String,
    val role: MemberRole,
    val isTerm: Boolean = true,
    val isMarketing: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
) {
    constructor(
        email: String,
        password: String,
        name: String,
        role: MemberRole,
        isTerm: Boolean = true,
        isMarketing: Boolean = false
    ) : this(
        memberId = null,
        email = email,
        password = password,
        name = name,
        role = role,
        isTerm = isTerm,
        isMarketing = isMarketing
    )
}
