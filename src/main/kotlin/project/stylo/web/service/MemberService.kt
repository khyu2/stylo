package project.stylo.web.service

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import project.stylo.common.exception.BaseException
import project.stylo.common.s3.FileStorageService
import project.stylo.common.utils.SecurityUtils
import project.stylo.web.dao.MemberDao
import project.stylo.web.domain.Member
import project.stylo.web.domain.enums.ImageOwnerType
import project.stylo.web.domain.enums.MemberRole
import project.stylo.web.dto.request.MemberCreateRequest
import project.stylo.web.dto.request.MemberUpdateRequest
import project.stylo.web.dto.response.MemberResponse
import project.stylo.web.exception.MemberExceptionType

@Service
@Transactional
class MemberService(
    private val memberDao: MemberDao,
    private val passwordEncoder: PasswordEncoder,
    private val fileStorageService: FileStorageService,
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

    fun updateProfile(member: Member, request: MemberUpdateRequest) {
        member.apply {
            name = request.name ?: name
            isMarketing = request.isMarketing ?: isMarketing
        }

        memberDao.update(member).also {
            SecurityUtils.updateProfile(member)
        }
    }

    fun updatePassword(member: Member, request: MemberUpdateRequest) {
        if (!passwordEncoder.matches(request.currentPassword, member.password)) {
            throw BaseException(MemberExceptionType.PASSWORD_MISMATCH)
        }

        member.apply {
            password = passwordEncoder.encode(request.newPassword)
        }

        memberDao.updatePassword(member).also {
            SecurityUtils.updateProfile(member)
        }
    }

    // TODO: Fallback 처리 필요
    fun uploadProfileImage(
        member: Member,
        file: MultipartFile
    ): String {
        // S3 파일 업로드
        val fileUrl = fileStorageService.upload(file, member.memberId!!, ImageOwnerType.MEMBER)

        // 프로필 이미지 URL 업데이트
        memberDao.updateProfileImage(member.memberId, fileUrl)

        // S3에서 파일의 presigned URL 생성
        val profileUrl = fileStorageService.getPresignedUrl(fileUrl)

        // SecurityContextHolder에 저장된 Member 객체의 프로필 URL 업데이트
        SecurityUtils.updateProfileUrl(profileUrl)

        return profileUrl
    }

    fun deleteProfileImage(member: Member) {
        // 현재 프로필 이미지 URL 가져오기
        val currentImageUrl = member.profileUrl ?: throw BaseException(MemberExceptionType.PROFILE_NOT_FOUND)

        // 프로필 이미지 URL 업데이트
        memberDao.updateProfileImage(member.memberId!!, null).also {
            fileStorageService.delete(currentImageUrl)
        }

        // SecurityContextHolder에 저장된 Member 객체의 프로필 URL 업데이트
        SecurityUtils.updateProfileUrl(null)
    }
}