package project.stylo.common.s3.exception

import org.springframework.http.HttpStatus
import project.stylo.common.exception.ExceptionType

enum class FileExceptionType(
    override val status: HttpStatus,
    override val code: String,
    override val message: String
) : ExceptionType {
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다."),
    FILE_DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_DOWNLOAD_FAILED", "파일 다운로드에 실패했습니다."),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_DELETE_FAILED", "파일 삭제에 실패했습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "INVALID_FILE_NAME", "잘못된 파일명입니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "INVALID_FILE_EXTENSION", "잘못된 파일 확장자입니다."),
}