package project.stylo.web.dto.request

import org.springframework.format.annotation.DateTimeFormat
import project.stylo.web.domain.enums.ActionCode
import project.stylo.web.domain.enums.EventType
import java.time.LocalDate

data class ActionLogSearchRequest(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val startDate: LocalDate? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    val endDate: LocalDate? = null,
    val userKeyword: String? = null, // 사용자 ID, 이메일, 닉네임
    val action: ActionCode? = null,
    val eventType: EventType? = null,
    val ip: String? = null,
    val path: String? = null,
)
