package project.stylo.web.controller

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import project.stylo.auth.resolver.Auth
import project.stylo.web.domain.Member
import project.stylo.web.dto.request.MemberCreateRequest
import project.stylo.web.dto.request.MemberUpdateRequest
import project.stylo.web.service.MemberService

@Controller
class AuthController(
    private val memberService: MemberService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthController::class.java)
    }

    @GetMapping("/login")
    fun login(): String {
        return "auth/login"
    }

    @GetMapping("/register")
    fun register(model: Model): String {
        model.addAttribute(
            "request", MemberCreateRequest(
                email = "",
                password = "",
                name = "",
                phone = "",
                isTerm = true,
                isMarketing = false
            )
        )
        return "auth/register"
    }

    @PostMapping("/register")
    fun register(
        @Valid @ModelAttribute("request") request: MemberCreateRequest,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.info("회원가입 요청: email=${request.email}, name=${request.name}, isTerm=${request.isTerm}, isMarketing=${request.isMarketing}")

        if (bindingResult.hasErrors()) {
            logger.warn("회원가입 요청에 오류가 있습니다: ${bindingResult.allErrors.joinToString(", ")}")

            return "auth/register"
        }

        // 비밀번호 확인 검증
        if (request.password != request.confirmPassword) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch", "비밀번호가 일치하지 않습니다.")
            return "auth/register"
        }

        memberService.createMember(request)

        redirectAttributes.addFlashAttribute("success", "회원가입이 완료되었습니다.")
        return "redirect:/login?success=true"
    }

    // OAuth2 첫 로그인 시 연락처 입력 강제 페이지
    @GetMapping("/auth/contact")
    fun contactForm(@Auth member: Member, model: Model): String {
        model.addAttribute("request", MemberUpdateRequest(phone = member.phone))
        return "auth/contact"
    }

    @PostMapping("/auth/contact")
    fun submitContact(
        @Auth member: Member,
        @Valid @ModelAttribute("request") request: MemberUpdateRequest,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (request.phone.isNullOrBlank()) {
            bindingResult.rejectValue("phone", "phone.required", "휴대폰 번호를 입력해주세요.")
        }
        if (bindingResult.hasErrors()) {
            return "auth/contact"
        }

        memberService.updateProfile(member, request)
        redirectAttributes.addFlashAttribute("success", "연락처가 저장되었습니다.")
        return "redirect:/"
    }
}