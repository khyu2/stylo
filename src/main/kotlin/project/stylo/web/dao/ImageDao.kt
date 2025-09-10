package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JImage
import org.springframework.stereotype.Repository
import project.stylo.web.domain.enums.ImageOwnerType

@Repository
class ImageDao(private val dsl: DSLContext) {
    companion object {
        private val IMAGE = JImage.IMAGE
    }

    fun save(ownerId: Long, ownerType: ImageOwnerType, imageUrl: String) {
        dsl.insertInto(IMAGE)
            .set(IMAGE.OWNER_ID, ownerId)
            .set(IMAGE.OWNER_TYPE, ownerType.name)
            .set(IMAGE.IMAGE_URL, imageUrl)
            .execute()
    }

    fun findAllByProductId(productId: Long): List<String> =
        dsl.select(IMAGE.IMAGE_URL)
            .from(IMAGE)
            .where(IMAGE.OWNER_TYPE.eq(ImageOwnerType.PRODUCT.name))
            .and(IMAGE.OWNER_ID.eq(productId))
            .fetchInto(String::class.java)

    fun findAllByProductIds(productIds: List<Long>): Map<Long, List<String>> =
        dsl.select(IMAGE.OWNER_ID, IMAGE.IMAGE_URL)
            .from(IMAGE)
            .where(IMAGE.OWNER_TYPE.eq(ImageOwnerType.PRODUCT.name))
            .and(IMAGE.OWNER_ID.`in`(productIds))
            .fetch()
            .groupBy(
                { it.get(IMAGE.OWNER_ID) as Long },
                { it.get(IMAGE.IMAGE_URL) as String }
            )
}