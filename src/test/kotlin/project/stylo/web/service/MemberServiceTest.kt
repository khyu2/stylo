package project.stylo.web.service

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.multipart.MultipartFile
import project.stylo.auth.service.dto.MemberDetails
import project.stylo.common.exception.BaseException
import project.stylo.common.s3.FileStorageService
import project.stylo.web.dao.AddressDao
import project.stylo.web.dao.MemberDao
import project.stylo.web.domain.Address
import project.stylo.web.domain.Member
import project.stylo.web.domain.enums.ImageOwnerType
import project.stylo.web.domain.enums.MemberRole
import project.stylo.web.dto.request.AddressRequest
import project.stylo.web.dto.request.MemberCreateRequest
import project.stylo.web.dto.request.MemberUpdateRequest
import project.stylo.web.exception.MemberExceptionType

class MemberServiceTest : StringSpec({
    // Mocks
    lateinit var service: MemberService

    lateinit var memberDao: MemberDao
    lateinit var addressDao: AddressDao
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var fileStorageService: FileStorageService

    beforeTest {
        memberDao = mockk(relaxed = true)
        addressDao = mockk(relaxed = true)
        passwordEncoder = mockk(relaxed = true)
        fileStorageService = mockk(relaxed = true)
        service = MemberService(memberDao, addressDao, passwordEncoder, fileStorageService)
    }

    // -- createMember --
    "createMember는 비밀번호를 암호화하여 저장하고 MemberResponse를 반환한다" {
        // given
        val req = MemberCreateRequest(
            email = "user@example.com",
            password = "plainPass123",
            phone = "010-1234-5678",
            confirmPassword = "plainPass123",
            name = "홍길동",
            isTerm = true,
            isMarketing = null
        )
        every { memberDao.existsByEmail(req.email) } returns false
        every { passwordEncoder.encode(req.password) } returns "ENCODED"

        // 캡처: DAO에 저장되는 Member
        val memberSlot = slot<Member>()
        every { memberDao.save(capture(memberSlot)) } answers {
            val m = memberSlot.captured
            // DB 저장 이후 조회되어 돌아오는 형태를 시뮬레이션
            m.copy(memberId = 1L)
        }

        // when
        val res = service.createMember(req)

        // then
        memberSlot.captured.password shouldBe "ENCODED"
        res.memberId shouldBe 1L
        res.email shouldBe req.email
        res.name shouldBe req.name
        res.role shouldBe MemberRole.USER.name
        res.isTerm shouldBe true
        res.isMarketing shouldBe false // null -> false 처리
        verify { memberDao.existsByEmail(req.email) }
        verify { memberDao.save(any()) }
    }

    "createMember는 이메일 중복이면 MEMBER_ALREADY_EXISTS 예외를 던진다" {
        // given
        val req = MemberCreateRequest(
            email = "dup@example.com",
            password = "password123",
            phone = "010-1111-2222",
            confirmPassword = "password123",
            name = "중복",
            isTerm = true,
            isMarketing = false
        )
        every { memberDao.existsByEmail(req.email) } returns true

        // when & then
        shouldThrowExactly<BaseException> {
            service.createMember(req)
        }.exceptionType shouldBe MemberExceptionType.MEMBER_ALREADY_EXISTS
    }

    // -- updatePassword --
    "updatePassword는 현재 비밀번호가 일치하지 않으면 PASSWORD_MISMATCH 예외" {
        // given
        val member = Member(
            memberId = 10L,
            email = "user@ex.com",
            password = "HASHED",
            name = "유저",
            phone = "010-0000-0000",
            role = MemberRole.USER
        )
        val req =
            MemberUpdateRequest(currentPassword = "wrong", newPassword = "newpass123", confirmPassword = "newpass123")
        every { passwordEncoder.matches(req.currentPassword, member.password) } returns false

        // when & then
        shouldThrowExactly<BaseException> {
            service.updatePassword(member, req)
        }.exceptionType shouldBe MemberExceptionType.PASSWORD_MISMATCH
    }

    // -- address default logic --
    "createAddress는 첫 배송지면 기본 배송지로 설정한다" {
        // given
        val member = Member(
            memberId = 22L,
            email = "a@b.com",
            password = "p",
            name = "a",
            phone = "010-0000-0000",
            role = MemberRole.USER
        )
        every { addressDao.findAllByMemberId(member.memberId!!) } returns emptyList()

        val req = AddressRequest(
            recipient = "수취인",
            phone = "010-2222-3333",
            address = "서울시 어딘가",
            addressDetail = null,
            postalCode = "01234",
            requestMessage = "문앞",
            defaultAddress = false // 사용자 체크 X
        )

        val captured = slot<AddressRequest>()
        every { addressDao.save(member.memberId!!, capture(captured)) } answers {
            val r = captured.captured
            Address(
                addressId = 1L,
                memberId = member.memberId!!,
                recipient = r.recipient,
                phone = r.phone,
                address = r.address,
                addressDetail = r.addressDetail,
                postalCode = r.postalCode,
                requestMessage = r.requestMessage,
                defaultAddress = r.defaultAddress
            )
        }

        // when
        val res = service.createAddress(member, req)

        // then - 저장 직전 요청에 defaultAddress가 true로 바뀌었는지 검증
        captured.captured.defaultAddress shouldBe true
        res.defaultAddress shouldBe true
        verify(exactly = 0) { addressDao.resetDefaultAddress(any()) }
    }

    // -- profile image --
    "deleteProfileImage는 프로필이 없으면 PROFILE_NOT_FOUND 예외" {
        val member = Member(
            memberId = 33L,
            email = "none@p.com",
            password = "p",
            name = "n",
            phone = "010-0000-0000",
            role = MemberRole.USER,
            isTerm = true,
            isMarketing = false,
            profileUrl = null
        )

        shouldThrowExactly<BaseException> {
            service.deleteProfileImage(member)
        }.exceptionType shouldBe MemberExceptionType.PROFILE_NOT_FOUND
        verify(exactly = 0) { fileStorageService.delete(any()) }
    }

    "deleteProfileImage는 파일을 삭제하고 DAO를 통해 프로필을 null로 갱신한다" {
        // given
        val member = Member(
            memberId = 44L,
            email = "p@p.com",
            password = "p",
            name = "p",
            phone = "010-0000-0000",
            role = MemberRole.USER,
            profileUrl = "s3://bucket/path.png"
        )
        every { memberDao.updateProfileImage(member.memberId!!, null) } returns member.copy(profileUrl = null)
        every { fileStorageService.delete(member.profileUrl!!) } just runs
        val auth = mockk<Authentication>(relaxed = true)
        every { auth.principal } returns MemberDetails(member)
        SecurityContextHolder.getContext().authentication = auth

        // when
        service.deleteProfileImage(member)

        // then
        verify { memberDao.updateProfileImage(member.memberId!!, null) }
        verify { fileStorageService.delete("s3://bucket/path.png") }
    }

    // -- uploadProfileImage happy path (간단 검증) --
    "uploadProfileImage는 파일 저장 후 presigned-url을 반환한다" {
        val member = Member(
            memberId = 55L,
            email = "u@u.com",
            password = "p",
            name = "u",
            phone = "010-0000-0000",
            role = MemberRole.USER
        )
        val file = mockk<MultipartFile>()
        every { fileStorageService.upload(file, ImageOwnerType.MEMBER, member.memberId) } returns "origin/path"
        every { memberDao.updateProfileImage(member.memberId!!, any()) } returns member
        every { fileStorageService.getPresignedUrl("origin/path") } returns "signed/url"
        val auth = mockk<Authentication>(relaxed = true)
        every { auth.principal } returns MemberDetails(member)
        SecurityContextHolder.getContext().authentication = auth

        val url = service.uploadProfileImage(member, file)
        url shouldBe "signed/url"

        verify { fileStorageService.upload(file, ImageOwnerType.MEMBER, member.memberId) }
        verify { memberDao.updateProfileImage(member.memberId!!, "origin/path") }
        verify { fileStorageService.getPresignedUrl("origin/path") }
    }
})
