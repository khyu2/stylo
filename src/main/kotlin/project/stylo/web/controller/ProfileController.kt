package project.stylo.web.controller

import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import project.stylo.auth.resolver.Auth
import project.stylo.web.domain.Member
import project.stylo.web.dto.request.MemberUpdateRequest
import project.stylo.web.service.MemberService

@Controller
@RequestMapping("/profile")
class ProfileController(
    private val memberService: MemberService
) {
    @GetMapping
    fun profilePage(@Auth member: Member, model: Model): String {
        model.addAttribute("member", member)
        return "profile/index"
    }

    @PostMapping("/update")
    fun updateProfile(
        @Auth member: Member,
        @Valid @ModelAttribute request: MemberUpdateRequest,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
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
