package project.stylo.web.domain

import java.time.LocalDateTime

data class OptionType(
    val optionTypeId: Long,
    val name: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
