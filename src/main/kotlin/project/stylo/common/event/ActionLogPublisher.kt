package project.stylo.common.event

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import project.stylo.web.domain.ActionLogPayload

/**
 * Publishes user action events to be handled asynchronously by listeners.
 *
 * Usage example:
 *
 *     actionLogPublisher.publish(
 *         ActionLogPayload(
 *             eventType = "VIEW",
 *             action = "product.view",
 *             memberId = member.id,
 *             path = "/products/$productId",
 *             metadata = mapOf("productId" to productId)
 *         )
 *     )
 */
@Component
class ActionLogPublisher(
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun publish(payload: ActionLogPayload) {
        eventPublisher.publishEvent(UserActionEvent(this, payload))
    }
}
