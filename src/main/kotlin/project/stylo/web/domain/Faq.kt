package project.stylo.web.domain

import java.time.LocalDateTime

data class Faq(
    val faqId: Long,
    val question: String,
    val answer: String,
    val category: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val createdBy: Long
)