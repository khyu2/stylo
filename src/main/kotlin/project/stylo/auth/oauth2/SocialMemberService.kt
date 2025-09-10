package project.stylo.auth.oauth2

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.web.dao.MemberDao
import project.stylo.web.domain.Member
import project.stylo.web.domain.enums.MemberRole
import java.util.*

@Service
class SocialMemberService(
    private val memberDao: MemberDao,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun findOrCreate(info: SocialMember): Member {
        val email = info.email ?: syntheticEmail(info)
        val existing = memberDao.findByEmail(email)
        if (existing != null) return existing

        val randomPassword = passwordEncoder.encode("SOCIAL:" + UUID.randomUUID().toString())
        val phone = ""
        val name = info.name ?: email

        val member = Member(
            email = email,
            password = randomPassword,
            name = name,
            phone = phone,
            role = MemberRole.USER,
            isTerm = true,
            isMarketing = false,
        )
        return memberDao.save(member)
    }

    private fun syntheticEmail(info: SocialMember): String {
        return "${'$'}{info.provider.name.lowercase()}_${'$'}{info.providerId}@social.local"
    }
}
