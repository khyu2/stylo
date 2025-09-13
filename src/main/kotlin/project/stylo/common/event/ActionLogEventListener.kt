package project.stylo.common.event

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import project.stylo.web.dao.ActionLogRepository

@Component
class ActionLogEventListener(
    private val repository: ActionLogRepository,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ActionLogEventListener::class.java)
    }

    @Async
    @EventListener
    fun onUserActionEvent(event: UserActionEvent) {
        try {
            val saved = repository.save(event.payload.toDocument())
            if (logger.isDebugEnabled) {
                logger.debug("로그를 저장했습니다: {}", saved)
            }
        } catch (e: Exception) {
            logger.warn("데이터베이스에 로그를 저장하지 못했습니다.: {}", e.message)
        }
    }
}
