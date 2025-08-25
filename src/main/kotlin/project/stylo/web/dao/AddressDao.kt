package project.stylo.web.dao

import org.jooq.DSLContext
import org.jooq.generated.tables.JAddress
import org.springframework.stereotype.Repository
import project.stylo.common.exception.BaseException
import project.stylo.common.exception.BaseExceptionType
import project.stylo.web.domain.Address
import project.stylo.web.dto.request.AddressRequest

@Repository
class AddressDao(private val dsl: DSLContext) {
    companion object {
        private val ADDRESS = JAddress.ADDRESS
    }

    fun findById(id: Long): Address? =
        dsl.selectFrom(ADDRESS)
            .where(ADDRESS.ADDRESS_ID.eq(id))
            .fetchOneInto(Address::class.java)

    fun findAllByMemberId(memberId: Long): List<Address> =
        dsl.selectFrom(ADDRESS)
            .where(ADDRESS.MEMBER_ID.eq(memberId))
            .orderBy(ADDRESS.DEFAULT_ADDRESS.desc(), ADDRESS.ADDRESS_ID.desc())
            .fetchInto(Address::class.java)

    fun existsDefaultAddress(memberId: Long): Boolean =
        dsl.fetchExists(
            dsl.selectOne()
                .from(ADDRESS)
                .where(ADDRESS.MEMBER_ID.eq(memberId))
                .and(ADDRESS.DEFAULT_ADDRESS.eq(true))
        )

    fun resetDefaultAddress(memberId: Long) {
        dsl.update(ADDRESS)
            .set(ADDRESS.DEFAULT_ADDRESS, false)
            .where(ADDRESS.MEMBER_ID.eq(memberId))
            .execute()
    }

    fun updateDefaultAddress(addressId: Long) {
        dsl.update(ADDRESS)
            .set(ADDRESS.DEFAULT_ADDRESS, true)
            .where(ADDRESS.ADDRESS_ID.eq(addressId))
            .execute()
    }

    fun save(memberId: Long, request: AddressRequest): Address {
        val id = dsl.insertInto(ADDRESS)
            .set(ADDRESS.MEMBER_ID, memberId)
            .set(ADDRESS.RECIPIENT, request.recipient)
            .set(ADDRESS.PHONE, request.phone)
            .set(ADDRESS.ADDRESS_, request.address)
            .set(ADDRESS.ADDRESS_DETAIL, request.addressDetail)
            .set(ADDRESS.POSTAL_CODE, request.postalCode)
            .set(ADDRESS.DEFAULT_ADDRESS, request.defaultAddress)
            .returning(ADDRESS.ADDRESS_ID)
            .fetchOne(ADDRESS.ADDRESS_ID)!!

        return findById(id) ?: throw BaseException(BaseExceptionType.INTERNAL_SERVER_ERROR)
    }

    fun update(address: Address): Address {
        dsl.update(ADDRESS)
            .set(ADDRESS.RECIPIENT, address.recipient)
            .set(ADDRESS.PHONE, address.phone)
            .set(ADDRESS.ADDRESS_, address.address)
            .set(ADDRESS.ADDRESS_DETAIL, address.addressDetail)
            .set(ADDRESS.POSTAL_CODE, address.postalCode)
            .set(ADDRESS.DEFAULT_ADDRESS, address.defaultAddress)
            .where(ADDRESS.ADDRESS_ID.eq(address.addressId))
            .execute()

        return findById(address.addressId) ?: throw BaseException(BaseExceptionType.INTERNAL_SERVER_ERROR)
    }

    fun delete(id: Long) =
        dsl.deleteFrom(ADDRESS)
            .where(ADDRESS.ADDRESS_ID.eq(id))
            .execute()
}