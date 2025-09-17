package project.stylo.web.dao

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import project.stylo.web.domain.ActionLog
import project.stylo.web.dto.request.ActionLogSearchRequest

interface ActionLogRepositoryCustom {
    fun search(request: ActionLogSearchRequest, pageable: Pageable): Page<ActionLog>
}
