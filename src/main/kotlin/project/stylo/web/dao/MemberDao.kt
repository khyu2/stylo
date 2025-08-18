package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JMember
import org.springframework.stereotype.Repository
import project.stylo.web.domain.Member

@Repository
class MemberDao(
    private val dsl: DSLContext
) {

    companion object {
        private val MEMBER = JMember.MEMBER
    }

    fun findById(id: Long) = dsl
        .selectFrom(MEMBER)
        .where(MEMBER.MEMBER_ID.eq(id))
        .fetchOneInto(Member::class.java)

    fun findByEmail(email: String) = dsl
        .selectFrom(MEMBER)
        .where(MEMBER.EMAIL.eq(email))
        .fetchOneInto(Member::class.java)
}