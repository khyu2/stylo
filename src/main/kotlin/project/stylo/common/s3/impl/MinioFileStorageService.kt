package project.stylo.common.s3.impl

import io.minio.*
import io.minio.http.Method
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import project.stylo.common.exception.BaseException
import project.stylo.common.s3.FileStorageService
import project.stylo.common.s3.exception.FileExceptionType
import project.stylo.web.domain.enums.ImageOwnerType
import java.util.*

@Service
class MinioFileStorageService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket}") private val bucketName: String
) : FileStorageService {

    companion object {
        private val logger = LoggerFactory.getLogger(MinioFileStorageService::class.java)
        private val ALLOWED_EXTENSIONS = setOf(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp"
        )
    }

    override fun upload(file: MultipartFile, ownerId: Long, ownerType: ImageOwnerType): String {
        val extension = file.contentType ?: throw BaseException(FileExceptionType.INVALID_FILE_NAME)

        println("확장자 로그: $extension")

        // 확장자 검증
        if (extension.lowercase() !in ALLOWED_EXTENSIONS) {
            throw BaseException(FileExceptionType.INVALID_FILE_EXTENSION)
        }

        // 파일 이름 생성
        val uuid = UUID.randomUUID().toString().substring(0, 8)
        val fileName = "${ownerType.name.lowercase()}/$ownerId/${uuid}.$extension"

        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(fileName)
                    .stream(file.inputStream, file.size, -1)
                    .contentType(file.contentType)
                    .build()
            ).also {
                logger.info("🚀 파일이 성공적으로 업로드되었습니다: $fileName")
            }

            return fileName
        } catch (e: Exception) {
            logger.error("❌ 파일 업로드 중 오류 발생: ${e.message}", e)
            throw BaseException(FileExceptionType.FILE_UPLOAD_FAILED, e)
        }
    }

    override fun download(fileUrl: String): ByteArray {
        return try {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(fileUrl)
                    .build()
            ).readBytes().also {
                logger.info("📥 파일이 성공적으로 다운로드되었습니다: $fileUrl")
            }
        } catch (e: Exception) {
            logger.error("❌ 파일 다운로드 중 오류 발생: ${e.message}", e)
            throw BaseException(FileExceptionType.FILE_DOWNLOAD_FAILED, e)
        }
    }

    override fun getPresignedUrl(fileUrl: String, expireSeconds: Int): String {
        return try {
            minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .`object`(fileUrl)
                    .expiry(expireSeconds)
                    .build()
            ).also {
                logger.info("🔗 Presigned URL이 성공적으로 생성되었습니다: $it")
            }
        } catch (e: Exception) {
            logger.error("❌ Presigned URL 생성 중 오류 발생: ${e.message}", e)
            throw BaseException(FileExceptionType.FILE_DOWNLOAD_FAILED, e)
        }
    }

    override fun delete(fileUrl: String) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(fileUrl)
                    .build()
            ).also {
                logger.info("🗑️ 파일이 성공적으로 삭제되었습니다: $fileUrl")
            }
        } catch (e: Exception) {
            logger.error("❌ 파일 삭제 중 오류 발생: ${e.message}", e)
            throw BaseException(FileExceptionType.FILE_DELETE_FAILED, e)
        }
    }

    override fun deleteAllByOwner(ownerId: Long, ownerType: ImageOwnerType) {
        val prefix = "${ownerType.name.lowercase()}/$ownerId/"

        try {
            minioClient.listObjects(
                ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(true)
                    .build()
            ).forEach { delete(it.get().objectName()) }.also {
                logger.info("🗑️ 모든 파일이 성공적으로 삭제되었습니다: $prefix")
            }
        } catch (e: Exception) {
            logger.error("❌ 모든 파일 삭제 중 오류 발생: ${e.message}", e)
            throw BaseException(FileExceptionType.FILE_DELETE_FAILED, e)
        }
    }
}