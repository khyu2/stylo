package project.stylo.auth.service.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import project.stylo.web.domain.Member

/**
 * SecurityContext에 저장되는 인증된 사용자 정보
 * @param member 회원 정보
 * @param profileUrl 프로필 이미지 URL
 * @param cartCount 장바구니 상품 개수
 * @see project.stylo.auth.utils.SecurityUtils
 */
data class MemberDetails(
    var member: Member,
    var profileUrl: String? = null,
    var cartCount: Long? = null,
) : UserDetails {
    constructor(member: Member) : this(member, null, null)

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${member.role.name}"))

    override fun getUsername(): String = member.email

    override fun getPassword(): String = member.password

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}

