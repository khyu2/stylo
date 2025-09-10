package project.stylo.web.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import project.stylo.common.exception.BaseException
import project.stylo.common.s3.FileStorageService
import project.stylo.auth.utils.SecurityUtils
import project.stylo.web.dao.AddressDao
import project.stylo.web.dao.MemberDao
import project.stylo.web.domain.Member
import project.stylo.web.domain.enums.ImageOwnerType
import project.stylo.web.domain.enums.MemberRole
import project.stylo.web.dto.request.AddressRequest
import project.stylo.web.dto.request.MemberCreateRequest
import project.stylo.web.dto.request.MemberUpdateRequest
import project.stylo.web.dto.response.AddressResponse
import project.stylo.web.dto.response.MemberResponse
import project.stylo.web.exception.MemberExceptionType

@Service
@Transactional
class MemberService(
    private val memberDao: MemberDao,
    private val addressDao: AddressDao,
    private val passwordEncoder: PasswordEncoder,
    private val fileStorageService: FileStorageService,
) {
    fun createMember(request: MemberCreateRequest): MemberResponse {
        if (memberDao.existsByEmail(request.email)) {
            throw BaseException(MemberExceptionType.MEMBER_ALREADY_EXISTS)
        }

        // 비밀번호 암호화
        return memberDao.save(
            Member(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                name = request.name,
                phone = request.phone,
                role = MemberRole.USER,
                isTerm = request.isTerm,
                isMarketing = request.isMarketing ?: false
            )
        ).let(MemberResponse::from)
    }

    fun updateProfile(member: Member, request: MemberUpdateRequest) {
        member.apply {
            name = request.name ?: name
            phone = request.phone ?: phone
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

    fun getAddresses(member: Member): List<AddressResponse> =
        addressDao.findAllByMemberId(member.memberId!!).map { AddressResponse.from(it) }

    fun createAddress(member: Member, request: AddressRequest): AddressResponse {
        val addresses = addressDao.findAllByMemberId(member.memberId!!)

        val isDefault = when {
            addresses.isEmpty() -> true // 첫 배송지
            request.defaultAddress -> true // 사용자가 기본 체크함
            else -> false
        }

        if (isDefault && addresses.isNotEmpty()) {
            addressDao.resetDefaultAddress(member.memberId)
        }

        val address = addressDao.save(member.memberId, request.copy(defaultAddress = isDefault))
        return AddressResponse.from(address)
    }

    fun updateDefaultAddress(member: Member, addressId: Long) {
        val address = addressDao.findById(addressId) ?: throw BaseException(MemberExceptionType.ADDRESS_NOT_FOUND)

        if (address.defaultAddress) {
            return
        }

        // 기본 주소 설정
        if (addressDao.existsDefaultAddress(member.memberId!!)) {
            addressDao.resetDefaultAddress(member.memberId)
        }

        addressDao.updateDefaultAddress(addressId)
    }

    fun updateAddress(member: Member, addressId: Long, request: AddressRequest) {
        val address = addressDao.findById(addressId) ?: throw BaseException(MemberExceptionType.ADDRESS_NOT_FOUND)

        val updatedAddress = address.copy(
            recipient = request.recipient,
            phone = request.phone,
            address = request.address,
            addressDetail = request.addressDetail ?: address.addressDetail,
            postalCode = request.postalCode,
            defaultAddress = request.defaultAddress,
        )

        // 기본 주소 설정
        if (updatedAddress.defaultAddress && addressDao.existsDefaultAddress(member.memberId!!)) {
            addressDao.resetDefaultAddress(member.memberId)
        } else if (!updatedAddress.defaultAddress && !addressDao.existsDefaultAddress(member.memberId!!)) {
            throw BaseException(MemberExceptionType.DEFAULT_ADDRESS_NOT_FOUND)
        }

        addressDao.update(updatedAddress)
    }

    fun deleteAddress(member: Member, addressId: Long) {
        val addresses = addressDao.findAllByMemberId(member.memberId!!)
            .takeIf { it.size > 1 } ?: throw BaseException(MemberExceptionType.FAILED_DELETE_ADDRESS)

        // 삭제할 주소가 기본 주소인지 확인
        val address = addresses.find { it.addressId == addressId }
            ?: throw BaseException(MemberExceptionType.ADDRESS_NOT_FOUND)

        addressDao.delete(addressId)

        // 삭제한 주소가 기본 주소였다면, 남은 주소 중 첫 번째를 기본 주소로 설정
        if (address.defaultAddress) {
            val remainingAddresses = addressDao.findAllByMemberId(member.memberId)
            if (remainingAddresses.isNotEmpty()) {
                addressDao.updateDefaultAddress(remainingAddresses.first().addressId)
            }
        }
    }

    // TODO: Fallback 처리 필요
    fun uploadProfileImage(
        member: Member,
        file: MultipartFile
    ): String {
        // S3 파일 업로드
        val fileUrl = fileStorageService.upload(file, ImageOwnerType.MEMBER, member.memberId)

        // 프로필 이미지 URL 업데이트
        memberDao.updateProfileImage(member.memberId!!, fileUrl)

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