package project.stylo.auth.service.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import project.stylo.web.domain.Member

data class MemberDetails(var member: Member, var profileUrl: String?) : UserDetails {
    constructor(member: Member) : this(member, null)

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${member.role.name}"))

    override fun getUsername(): String = member.email

    override fun getPassword(): String = member.password

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}

