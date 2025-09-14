package project.stylo.common.event

import org.springframework.context.ApplicationEvent
import project.stylo.web.domain.ActionLogPayload

class UserActionEvent(source: Any, val payload: ActionLogPayload) : ApplicationEvent(source)
