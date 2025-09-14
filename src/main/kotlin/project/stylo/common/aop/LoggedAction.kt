package project.stylo.common.aop

import project.stylo.web.domain.enums.ActionCode
import project.stylo.web.domain.enums.EventType

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class LoggedAction(
    val eventType: EventType,
    val action: ActionCode,
    val includeArgs: Boolean = false,
)