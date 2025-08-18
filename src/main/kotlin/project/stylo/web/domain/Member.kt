package project.stylo.web.domain

import project.stylo.web.domain.enums.MemberRole
import java.time.LocalDateTime

data class Member(
    val memberId: Long,
    val email: String,
    val password: String,
    val name: String,
    val role: MemberRole,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
)
