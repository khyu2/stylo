package project.stylo.web.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import project.stylo.auth.resolver.Auth
import project.stylo.common.response.BaseResponse
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
//        @AuthenticationPrincipal memberDetails: MemberDetails,
//        @RequestParam name: String,
//        @RequestParam email: String,

//        @RequestParam phone: String?,
//        @RequestParam birthDate: String?,
//        redirectAttributes: RedirectAttributes
//    ): String {
//        try {
//            memberService.updateProfile(
//                memberId = memberDetails.member.id,
//                name = name,
//                email = email,
//                phone = phone,
//                birthDate = birthDate
//            )
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
    @ResponseBody
    fun uploadProfileImage(
        @Auth member: Member,
        @RequestParam("image") image: MultipartFile
    ): BaseResponse<String> =
        BaseResponse.success(
            memberService.uploadProfileImage(member, image),
            "프로필 이미지가 성공적으로 업로드되었습니다."
        )

}
