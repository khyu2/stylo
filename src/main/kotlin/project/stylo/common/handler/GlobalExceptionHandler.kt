package project.stylo.common.handler

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.support.RedirectAttributes
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
        logger.error("오류가 발생했습니다: ${e.exceptionType.code} - ${e.exceptionType.message}")

        redirectAttributes.addFlashAttribute("error", e.exceptionType.message)
        val referer = request.getHeader("referer") ?: request.requestURI
        return "redirect:$referer"
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        e: Exception,
        request: HttpServletRequest,
        redirectAttributes: RedirectAttributes
    ): String {
        logger.error("알 수 없는 오류가 발생했습니다: ${e.message}", e)

        redirectAttributes.addFlashAttribute("error", "알 수 없는 오류가 발생했습니다. 관리자에게 문의해주세요.")
        val referer = request.getHeader("referer") ?: request.requestURI
        return "redirect:$referer"
    }
}