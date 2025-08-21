package project.stylo.web.controller

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import project.stylo.auth.resolver.Auth
import project.stylo.web.domain.Member
import project.stylo.web.dto.request.AddressRequest
import project.stylo.web.dto.request.MemberUpdateRequest
import project.stylo.web.service.MemberService

@Controller
@RequestMapping("/profile")
class ProfileController(
    private val memberService: MemberService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ProfileController::class.java)
    }

    @GetMapping
    fun profilePage(@Auth member: Member, model: Model): String {
        model.addAttribute("member", member)

        // 배송지 목록 추가
        val addresses = memberService.getAddresses(member.memberId!!)
        model.addAttribute("addresses", addresses)

        // AddressRequest 객체 추가 (폼 바인딩용)
        model.addAttribute(
            "addressRequest", AddressRequest(
                recipient = "",
                phone = "",
                address = "",
                addressDetail = null,
                postalCode = "",
                defaultAddress = false
            )
        )

        return "profile/index"
    }

    @PostMapping("/update")
    fun updateProfile(
        @Auth member: Member,
        @Valid @ModelAttribute request: MemberUpdateRequest,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("프로필 수정 요청: {}", request)

        if (bindingResult.hasErrors()) {
            return "redirect:/profile"
        }
        memberService.updateProfile(member, request)

        redirectAttributes.addFlashAttribute("success", "프로필이 성공적으로 수정되었습니다.")
        return "redirect:/profile"
    }

    @PostMapping("/change-password")
    fun changePassword(
        @Auth member: Member,
        @Valid @ModelAttribute request: MemberUpdateRequest,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (bindingResult.hasErrors()) {
            return "redirect:/profile"
        }
        memberService.updatePassword(member, request)

        redirectAttributes.addFlashAttribute("success", "비밀번호가 성공적으로 변경되었습니다.")
        return "redirect:/profile"
    }

    @PostMapping("/address")
    fun createAddress(
        @Auth member: Member,
        @Valid @ModelAttribute("addressRequest") request: AddressRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("배송지 등록 요청: {}", request)

        memberService.createAddress(member, request)
        redirectAttributes.addFlashAttribute("success", "주소가 성공적으로 업데이트되었습니다.")
        return "redirect:/profile"
    }

    @PostMapping("/address/default/{addressId}")
    fun updateDefaultAddress(
        @Auth member: Member,
        @PathVariable addressId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        memberService.updateDefaultAddress(member, addressId)
        redirectAttributes.addFlashAttribute("success", "기본 배송지가 성공적으로 변경되었습니다.")
        return "redirect:/profile"
    }

    @PostMapping("/address/update/{addressId}")
    fun updateAddress(
        @Auth member: Member,
        @Valid @ModelAttribute("addressRequest") request: AddressRequest,
        @PathVariable addressId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        memberService.updateAddress(member, addressId, request)
        redirectAttributes.addFlashAttribute("success", "주소가 성공적으로 수정되었습니다.")
        return "redirect:/profile"
    }

    @PostMapping("/address/delete/{addressId}")
    fun deleteAddress(
        @Auth member: Member,
        @PathVariable addressId: Long,
        redirectAttributes: RedirectAttributes
    ): String {
        memberService.deleteAddress(member, addressId)
        redirectAttributes.addFlashAttribute("success", "주소가 성공적으로 삭제되었습니다.")
        return "redirect:/profile"
    }

    @PostMapping("/upload-profile-image")
    fun uploadProfileImage(
        @Auth member: Member,
        @RequestParam("image") image: MultipartFile,
        redirectAttributes: RedirectAttributes
    ): String {
        memberService.uploadProfileImage(member, image)
        redirectAttributes.addFlashAttribute("success", "프로필 이미지가 성공적으로 업로드되었습니다.")
        return "redirect:/profile"
    }

    @PostMapping("/delete-profile-image")
    fun deleteProfileImage(
        @Auth member: Member,
        redirectAttributes: RedirectAttributes
    ): String {
        memberService.deleteProfileImage(member)
        redirectAttributes.addFlashAttribute("success", "프로필 이미지가 성공적으로 삭제되었습니다.")
        return "redirect:/profile"
    }

}
