package project.stylo.web.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import project.stylo.common.exception.BaseException
import project.stylo.web.dao.MemberDao
import project.stylo.web.domain.Member
import project.stylo.web.domain.enums.MemberRole
import project.stylo.web.dto.request.MemberCreateRequest
import project.stylo.web.dto.response.MemberResponse
import project.stylo.web.exception.MemberExceptionType

@Service
class MemberService(
    private val memberDao: MemberDao,
    private val passwordEncoder: PasswordEncoder,
) {
    fun createMember(request: MemberCreateRequest): MemberResponse {
        if (memberDao.existsByEmail(request.email)) {
            throw BaseException(MemberExceptionType.MEMBER_ALREADY_EXISTS)
        }

        return memberDao.save(
            Member(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                name = request.name,
                role = MemberRole.USER,
                isTerm = request.isTerm,
                isMarketing = request.isMarketing ?: false
            )
        ).let(MemberResponse::from)
    }
}