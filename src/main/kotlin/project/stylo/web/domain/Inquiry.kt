package project.stylo.web.domain

import project.stylo.web.domain.enums.InquiryStatus
import java.time.LocalDateTime

data class Inquiry(
    val inquiryId: Long,
    val memberId: Long,
    val title: String,
    val content: String,
    val status: InquiryStatus,
    val createdAt: LocalDateTime? = null
)