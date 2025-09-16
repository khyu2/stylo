package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JReview
import org.springframework.stereotype.Repository
import project.stylo.web.domain.Review

@Repository
class ReviewDao(private val dsl: DSLContext) {
    companion object {
        private val REVIEW = JReview.REVIEW
    }

    fun save(review: Review): Long =
        dsl.insertInto(REVIEW)
            .set(REVIEW.MEMBER_ID, review.memberId)
            .set(REVIEW.ORDER_ITEM_ID, review.orderItemId)
            .set(REVIEW.PRODUCT_ID, review.productId)
            .set(REVIEW.RATING, review.rating)
            .set(REVIEW.TITLE, review.title)
            .set(REVIEW.CONTENT, review.content)
            .returning(REVIEW.REVIEW_ID)
            .fetchOne(REVIEW.REVIEW_ID)!!

    fun existsByOrderItemId(orderItemId: Long) =
        dsl.fetchExists(
            dsl.selectFrom(REVIEW)
                .where(
                    REVIEW.ORDER_ITEM_ID.eq(orderItemId)
                        .and(REVIEW.DELETED_AT.isNotNull)
                )
        )

}