package project.stylo.web.domain

import project.stylo.web.domain.enums.ImageOwnerType
import java.time.LocalDateTime

data class Image(
    val imageId: Long,
    val ownerId: Long,
    val ownerType: ImageOwnerType,
    val imageUrl: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
