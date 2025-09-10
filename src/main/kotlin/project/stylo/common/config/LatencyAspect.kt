package project.stylo.common.config

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LatencyAspect {
    companion object {
        private val logger = LoggerFactory.getLogger(LatencyAspect::class.java)
    }

    @Around(
        "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
                "@annotation(org.springframework.web.bind.annotation.PostMapping)"
    )
    fun measureLatency(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.nanoTime()
        val result = joinPoint.proceed()
        val end = System.nanoTime()
        val durationMs = (end - start) / 1_000_000
        val logMessage =
            "API 경로: ${joinPoint.target.javaClass.simpleName}.${joinPoint.signature.name} 응답시간: ${durationMs}ms"
        if (durationMs >= 3000) {
            logger.warn("API 응답시간이 3초를 초과했습니다. $logMessage")
        } else {
            logger.info(logMessage)
        }
        return result
    }
}