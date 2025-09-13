package project.stylo.web.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "action_logs")
data class ActionLog(
    @Id
    val id: String? = null,
    val eventType: String, // e.g., CLICK, VIEW, ADD_TO_CART, ORDER_CREATED
    val action: String,    // e.g., "product.view", "cart.add"
    val memberId: Long?,   // nullable when guest
    val sessionId: String?,
    val ip: String?,
    val userAgent: String?,
    val path: String?,
    val params: Map<String, Any?>?,
    val metadata: Map<String, Any?>?,
    val timestamp: Instant = Instant.now(),
)

data class ActionLogPayload(
    val eventType: String,
    val action: String,
    val memberId: Long? = null,
    val sessionId: String? = null,
    val ip: String? = null,
    val userAgent: String? = null,
    val path: String? = null,
    val params: Map<String, Any?>? = null,
    val metadata: Map<String, Any?>? = null,
    val timestamp: Instant = Instant.now(),
) {
    fun toDocument(): ActionLog = ActionLog(
        eventType = eventType,
        action = action,
        memberId = memberId,
        sessionId = sessionId,
        ip = ip,
        userAgent = userAgent,
        path = path,
        params = params,
        metadata = metadata,
        timestamp = timestamp,
    )
}
