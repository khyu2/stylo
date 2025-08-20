package project.stylo.common.s3

import org.springframework.web.multipart.MultipartFile
import project.stylo.web.domain.enums.ImageOwnerType

interface FileStorageService {
    /**
     * 파일 업로드 메서드
     * @param file 업로드할 파일
     * @param ownerId 파일 소유자의 ID
     * @param ownerType 파일 소유자의 타입 (예: MEMBER, PRODUCT 등)
     * @return 업로드된 파일의 URL
     */
    fun upload(file: MultipartFile, ownerId: Long, ownerType: ImageOwnerType): String

    /**
     * 파일 다운로드 메서드
     * @param fileUrl 다운로드할 파일의 URL
     * @return 파일의 바이트 배열
     */
    fun download(fileUrl: String): ByteArray

    /**
     * 파일의 Presigned-URL을 생성하는 메서드
     * @param fileUrl 파일의 경로
     * @param expireSeconds URL의 만료 시간 (초 단위, 기본값: 3600초)
     * @return Presigned-URL
     */
    fun getPresignedUrl(fileUrl: String, expireSeconds: Int = 3600): String

    /**
     * 파일 삭제 메서드
     * @param fileUrl 삭제할 파일의 URL
     */
    fun delete(fileUrl: String)

    /**
     * 특정 소유자의 모든 파일을 삭제하는 메서드
     * @param ownerId 파일 소유자의 ID
     * @param ownerType 파일 소유자의 타입 (예: MEMBER, PRODUCT 등)
     */
    fun deleteAllByOwner(ownerId: Long, ownerType: ImageOwnerType)
}