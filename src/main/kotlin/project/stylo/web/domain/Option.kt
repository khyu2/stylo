package project.stylo.web.domain

import java.time.LocalDateTime

data class Option(
    val optionId: Long,
    val optionTypeId: Long,
    val value: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
