package project.stylo.common.handler

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.resource.NoResourceFoundException
import project.stylo.common.exception.BaseException

@ControllerAdvice
class GlobalExceptionHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(
        e: BaseException,
        request: HttpServletRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.warn("오류가 발생했습니다: ${e.exceptionType.code} - ${e.exceptionType.message}")

        redirectAttributes.addFlashAttribute("error", e.exceptionType.message)
        val referer = request.getHeader("referer") ?: request.requestURI
        return "redirect:$referer"
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(
        e: NoResourceFoundException,
    ): String {
        logger.error("페이지를 찾을 수 없습니다: ${e.message}")
        return "not-found"
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        val errorMessage = e.bindingResult.fieldErrors.joinToString(", ") { "${it.defaultMessage}" }
        logger.warn("유효성 검사 오류: $errorMessage")

        redirectAttributes.addFlashAttribute("error", errorMessage)
        val referer = request.getHeader("referer") ?: request.requestURI
        return "redirect:$referer"
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(
        e: AuthorizationDeniedException,
        request: HttpServletRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.warn("권한이 거부되었습니다: ${e.message}")

        redirectAttributes.addFlashAttribute("error", "권한이 없습니다.")
        return "redirect:/error"
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        e: Exception,
        request: HttpServletRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.warn("알 수 없는 오류가 발생했습니다: ${e.message}", e)

        redirectAttributes.addFlashAttribute("error", "알 수 없는 오류가 발생했습니다. 관리자에게 문의해주세요.")
        val referer = request.getHeader("referer") ?: request.requestURI
        return "redirect:$referer"
    }
}