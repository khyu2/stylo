package project.stylo.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("code", "message", "data", "pagination")
data class BaseResponse<T>(
    val success: Boolean = true,
    val message: String = "요청에 성공하였습니다.",

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val pagination: PaginationInfo? = null
) {
    data class PaginationInfo(
        val currentPage: Int,
        val totalPages: Int,
        val totalElements: Long,
        val pageSize: Int,
        val hasNext: Boolean,
        val hasPrevious: Boolean
    )

    companion object {
        @JvmStatic
        fun <T> success(data: T): BaseResponse<T> {
            return BaseResponse(true, "요청에 성공하였습니다.", data)
        }

        @JvmStatic
        fun <T> success(message: String): BaseResponse<T> {
            return BaseResponse(true, message)
        }

        @JvmStatic
        fun <T> success(data: T, message: String): BaseResponse<T> {
            return BaseResponse(true, message, data)
        }

        @JvmStatic
        fun <T> success(data: T, pagination: PaginationInfo): BaseResponse<T> {
            return BaseResponse(true, "요청에 성공하였습니다.", data, pagination)
        }

        @JvmStatic
        fun <T> success(data: T, message: String, pagination: PaginationInfo): BaseResponse<T> {
            return BaseResponse(true, message, data, pagination)
        }

        @JvmStatic
        fun <T> success(): BaseResponse<T> {
            return BaseResponse(true, "요청에 성공하였습니다.")
        }

        @JvmStatic
        fun <T> error(message: String): BaseResponse<T> {
            return BaseResponse(false, message)
        }

        @JvmStatic
        fun <T> error(data: T, message: String): BaseResponse<T> {
            return BaseResponse(false, message, data)
        }
    }
}