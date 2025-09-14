package project.stylo.common.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import project.stylo.common.event.ActionLogPublisher
import project.stylo.web.domain.Member

@Aspect
@Component
class ActionLogAspect(
    private val actionLogPublisher: ActionLogPublisher,
) {
    // LoggedAction 어노테이션이 붙은 메서드가 정상적으로 리턴한 후에 실행됨
    @AfterReturning("@annotation(loggedAction)")
    fun afterReturning(joinPoint: JoinPoint, loggedAction: LoggedAction) {
        val req = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request

        // 메서드 인자에서 Member 타입을 찾아 memberId 추출
        val memberId = joinPoint.args.firstOrNull { it is Member }?.let { (it as Member).memberId }

        val params: Map<String, Any?>? = if (loggedAction.includeArgs) buildParams(joinPoint) else null

        actionLogPublisher.publish(
            eventType = loggedAction.eventType,
            action = loggedAction.action,
            memberId = memberId,
            path = req?.requestURI,
            params = params,
            metadata = null,
        )
    }

    private fun buildParams(joinPoint: JoinPoint): Map<String, Any?> {
        val signature = joinPoint.signature as MethodSignature
        val names = signature.parameterNames
        val values = joinPoint.args
        val map = LinkedHashMap<String, Any?>()
        for (i in names.indices) {
            val name = names[i]
            val value = values.getOrNull(i)
            when (value) {
                // null 인 경우 null 로 저장
                null -> map[name] = null

                // 단순 타입은 그대로 저장
                is Number, is Boolean, is Char, is String -> {
                    if (value is String && name.contains("body", ignoreCase = true)) {
                        map["${name}Length"] = value.length
                    } else {
                        map[name] = value
                    }
                }

                // DTO 인 경우 toString() 호출
                // - Member: password 제외
                else -> {
                    val s = when (value) {
                        is Member -> "Member(memberId=${value.memberId}, email=${value.email})" // password 제외
                        else -> value.toString()
                    }
                    map[name] = if (s.length <= 120) s else s.take(117) + "..."
                }
            }
        }
        return map
    }
}