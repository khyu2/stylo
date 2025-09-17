package project.stylo.web.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import project.stylo.common.ua.UserAgentInfo
import project.stylo.web.domain.enums.ActionCode
import project.stylo.web.domain.enums.EventType
import java.time.Instant

@Document(collection = "action_logs")
data class ActionLog(
    @Id
    val id: String? = null,
    val eventType: EventType,
    val action: String,
    val memberId: Long?,
    val sessionId: String?,
    val ip: String?,
    val userAgent: String?,
    val path: String?,
    val params: Map<String, Any?>?,
    val metadata: Map<String, Any?>?,
    val timestamp: Instant = Instant.now(),
)

data class ActionLogPayload(
    val eventType: EventType,
    val action: ActionCode,
    val memberId: Long? = null,
    val sessionId: String? = null,
    val ip: String? = null,
    val userAgent: UserAgentInfo? = null,
    val path: String? = null,
    val params: Map<String, Any?>? = null,
    val metadata: Map<String, Any?>? = null,
    val timestamp: Instant = Instant.now(),
) {
    fun toDocument(): ActionLog = ActionLog(
        eventType = eventType,
        action = action.code,
        memberId = memberId,
        sessionId = sessionId,
        ip = ip,
        userAgent = userAgent.toString(),
        path = path,
        params = params,
        metadata = metadata,
        timestamp = timestamp,
    )
}
