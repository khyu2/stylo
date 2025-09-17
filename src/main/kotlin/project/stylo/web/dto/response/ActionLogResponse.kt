package project.stylo.web.dto.response

import project.stylo.web.domain.ActionLog
import project.stylo.web.domain.enums.ActionCode
import project.stylo.web.domain.enums.EventType

data class ActionLogResponse(
    val id: String,
    val eventType: EventType,
    val action: String,
    val memberId: Long?,
    val sessionId: String?,
    val ip: String?,
    val userAgent: String?,
    val path: String?,
    val params: Map<String, Any?>?,
    val metadata: Map<String, Any?>?,
    val timestamp: String,
) {
    companion object {
        fun from(actionLog: ActionLog): ActionLogResponse =
            ActionLogResponse(
                id = actionLog.id!!,
                eventType = actionLog.eventType,
                action = actionLog.action,
                memberId = actionLog.memberId,
                sessionId = actionLog.sessionId,
                ip = actionLog.ip,
                userAgent = actionLog.userAgent,
                path = actionLog.path,
                params = actionLog.params,
                metadata = actionLog.metadata,
                timestamp = actionLog.timestamp.toString(),
            )
    }
}