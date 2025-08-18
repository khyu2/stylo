package project.stylo.web.domain

import java.time.LocalDateTime

data class InquiryReply(
    val inquiryReplyId: Long,
    val inquiryId: Long,
    val adminId: Long,
    val content: String,
    val createdAt: LocalDateTime? = null
)