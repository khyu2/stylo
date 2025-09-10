package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JMember
import org.springframework.stereotype.Repository
import project.stylo.common.exception.BaseException
import project.stylo.common.exception.BaseExceptionType
import project.stylo.web.domain.Member
import java.time.LocalDateTime

@Repository
class MemberDao(private val dsl: DSLContext) {
    companion object {
        private val MEMBER = JMember.MEMBER
    }

    fun existsByEmail(email: String): Boolean =
        dsl.fetchExists(
            dsl.selectOne()
                .from(MEMBER)
                .where(MEMBER.EMAIL.eq(email))
        )

    fun findById(id: Long) =
        dsl.selectFrom(MEMBER)
            .where(MEMBER.MEMBER_ID.eq(id))
            .fetchOneInto(Member::class.java)

    fun findByEmail(email: String) =
        dsl.selectFrom(MEMBER)
            .where(MEMBER.EMAIL.eq(email))
            .fetchOneInto(Member::class.java)

    fun save(member: Member): Member {
        val id = dsl.insertInto(MEMBER)
            .set(MEMBER.EMAIL, member.email)
            .set(MEMBER.PASSWORD, member.password)
            .set(MEMBER.NAME, member.name)
            .set(MEMBER.ROLE, member.role.name)
            .set(MEMBER.IS_TERM, member.isTerm)
            .set(MEMBER.IS_MARKETING, member.isMarketing)
            .returning(MEMBER.MEMBER_ID)
            .fetchOne(MEMBER.MEMBER_ID)!!

        return findById(id) ?: throw BaseException(BaseExceptionType.INTERNAL_SERVER_ERROR)
    }

    fun update(member: Member) =
        dsl.update(MEMBER)
            .set(MEMBER.NAME, member.name)
            .set(MEMBER.PHONE, member.phone)
            .set(MEMBER.IS_MARKETING, member.isMarketing)
            .set(MEMBER.UPDATED_AT, LocalDateTime.now())
            .where(MEMBER.MEMBER_ID.eq(member.memberId))
            .execute()

    fun updatePassword(member: Member) =
        dsl.update(MEMBER)
            .set(MEMBER.PASSWORD, member.password)
            .set(MEMBER.UPDATED_AT, LocalDateTime.now())
            .where(MEMBER.MEMBER_ID.eq(member.memberId))
            .execute()

    fun updateProfileImage(id: Long, imageUrl: String?): Member {
        dsl.update(MEMBER)
            .set(MEMBER.PROFILE_URL, imageUrl)
            .set(MEMBER.UPDATED_AT, LocalDateTime.now())
            .where(MEMBER.MEMBER_ID.eq(id))
            .returning(MEMBER.MEMBER_ID)
            .execute()

        return findById(id) ?: throw BaseException(BaseExceptionType.INTERNAL_SERVER_ERROR)
    }

    fun countAll(): Int = dsl.fetchCount(dsl.selectFrom(MEMBER))
}