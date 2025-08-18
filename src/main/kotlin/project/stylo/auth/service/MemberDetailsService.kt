package project.stylo.auth.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import project.stylo.auth.service.dto.MemberDetails
import project.stylo.common.exception.BaseException
import project.stylo.common.exception.BaseExceptionType
import project.stylo.web.dao.MemberDao

@Service
class MemberDetailsService(
    private val memberDao: MemberDao
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val member = memberDao.findByEmail(username)
            ?: throw BaseException(BaseExceptionType.BAD_CREDENTIALS)
        return MemberDetails(member)
    }
}