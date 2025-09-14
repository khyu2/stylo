package project.stylo.common.event

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import project.stylo.common.ua.UserAgentParser
import project.stylo.web.domain.ActionLogPayload
import project.stylo.web.domain.enums.ActionCode
import project.stylo.web.domain.enums.EventType

@Component
class ActionLogPublisher(
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun publish(payload: ActionLogPayload) {
        eventPublisher.publishEvent(UserActionEvent(this, payload))
    }

    fun publish(
        eventType: EventType,
        action: ActionCode,
        memberId: Long? = null,
        path: String? = null,
        params: Map<String, Any?>? = null,
        metadata: Map<String, Any?>? = null,
    ) {
        val req = currentRequest()
        val uaHeader = req?.getHeader("User-Agent")
        val payload = ActionLogPayload(
            eventType = eventType,
            action = action,
            memberId = memberId,
            sessionId = req?.requestedSessionId ?: req?.session?.id,
            ip = req?.let { it.getHeader("X-Forwarded-For")?.split(",")?.firstOrNull()?.trim() ?: it.remoteAddr },
            userAgent = UserAgentParser.parse(uaHeader),
            path = path ?: req?.requestURI,
            params = params,
            metadata = metadata,
        )
        publish(payload)
    }

    private fun currentRequest(): HttpServletRequest? {
        val attrs = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        return attrs?.request
    }
}
