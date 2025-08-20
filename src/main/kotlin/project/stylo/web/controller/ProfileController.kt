package project.stylo.web.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import project.stylo.auth.resolver.Auth
import project.stylo.web.domain.Member
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

//    @PostMapping("/update")
//    fun updateProfile(
//        @Auth member: Member,
//        @ModelAttribute request: MemberUpdateRequest,
//        bindingResult: BindingResult,
//        redirectAttributes: RedirectAttributes
//    ): String {
//        try {
//            redirectAttributes.addFlashAttribute("success", "프로필이 성공적으로 수정되었습니다.")
//        } catch (e: Exception) {
//            redirectAttributes.addFlashAttribute("error", "프로필 수정에 실패했습니다: ${e.message}")
//        }
//        return "redirect:/profile"
//    }

//    @PostMapping("/change-password")
//    fun changePassword(
//        @AuthenticationPrincipal memberDetails: MemberDetails,
//        @RequestParam currentPassword: String,
//        @RequestParam newPassword: String,
//        @RequestParam confirmPassword: String,
//        redirectAttributes: RedirectAttributes
//    ): String {
//        try {
//            if (newPassword != confirmPassword) {
//                redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.")
//                return "redirect:/profile"
//            }
//
//            memberService.changePassword(
//                memberId = memberDetails.member.id,
//                currentPassword = currentPassword,
//                newPassword = newPassword
//            )
//            redirectAttributes.addFlashAttribute("success", "비밀번호가 성공적으로 변경되었습니다.")
//        } catch (e: Exception) {
//            redirectAttributes.addFlashAttribute("error", "비밀번호 변경에 실패했습니다: ${e.message}")
//        }
//        return "redirect:/profile"
//    }

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

    @PostMapping("/add-address")
    fun addAddress(
        @Auth member: Member,
        @RequestParam name: String,
        @RequestParam recipientName: String,
        @RequestParam phone: String,
        @RequestParam postalCode: String,
        @RequestParam address1: String,
        @RequestParam address2: String,
        @RequestParam(defaultValue = "false") isDefault: Boolean,
        redirectAttributes: RedirectAttributes
    ): String {
        return try {
            // TODO: 배송지 저장 로직 구현
            redirectAttributes.addFlashAttribute("success", "배송지가 성공적으로 저장되었습니다.")
            "redirect:/profile"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "배송지 저장에 실패했습니다: ${e.message}")
            "redirect:/profile"
        }
    }

}
