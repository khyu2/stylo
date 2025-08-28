package project.stylo.web.domain

import java.time.LocalDateTime

data class Category(
    val categoryId: Long,
    val name: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
)