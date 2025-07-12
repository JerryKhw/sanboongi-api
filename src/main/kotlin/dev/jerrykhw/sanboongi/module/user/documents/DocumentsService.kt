package dev.jerrykhw.sanboongi.module.user.documents

import dev.jerrykhw.sanboongi.config.R2Config
import dev.jerrykhw.sanboongi.entity.Document
import dev.jerrykhw.sanboongi.entity.User
import dev.jerrykhw.sanboongi.module.user.documents.dto.UpdateDocumentRequest
import dev.jerrykhw.sanboongi.repository.DocumentRepository
import dev.jerrykhw.sanboongi.util.nanoid.NanoId
import dev.jerrykhw.sanboongi.util.nanoid.PublicIdEntitySaver
import dev.jerrykhw.sanboongi.worker.DocumentQueueWorker
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalDateTime


@Service
class DocumentsService(
    private val documentRepository: DocumentRepository,
    private val publicIdEntitySaver: PublicIdEntitySaver,
    private val redisTemplate: StringRedisTemplate,
    private val s3Client: S3Client,
) {

    fun getDocuments(user: User): List<Document> {
        return documentRepository.findAllByUserIdOrderByUpdatedAtDesc(user.id)
    }

    fun getDocument(user: User, id: String): Document {
        val document = documentRepository.findByUserIdAndPublicId(user.id, id)

        if (document == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "not_found")
        }

        return document
    }

    fun updateDocument(user: User, id: String, request: UpdateDocumentRequest): Document {
        val saved = documentRepository.save(
            getDocument(user, id).copy(
                title = request.title,
                affiliation = request.affiliation,
                enrollmentDate = request.enrollmentDate,
                name = request.name,
                birthDate = request.birthDate,
                verifier = request.verifier,
                records = request.records,
                updatedAt = LocalDateTime.now()
            )
        )

        val id = saved.id.toString()

        redisTemplate.opsForList().remove(DocumentQueueWorker.queueKey, 0, id)
        redisTemplate.opsForList().leftPush(DocumentQueueWorker.queueKey, id)

        return saved
    }

    fun deleteDocument(user: User, id: String) {
        documentRepository.delete(
            getDocument(user, id)
        )
    }

    fun newDocument(user: User): Document {
        val saved = publicIdEntitySaver.saveWithNanoIdRetry(
            documentRepository, Document(
                publicId = NanoId.generate(),
                user = user,
                records = listOf(
                    mapOf(
                        "id" to NanoId.generate(),
                        "type" to "",
                        "startDate" to LocalDate.now().toString(),
                        "startTime" to "",
                        "endDate" to LocalDate.now().toString(),
                        "endTime" to "",
                        "duration" to "1",
                        "reason" to "",
                        "location" to "",
                        "accumulated" to "",
                        "managerApproval" to "",
                        "supervisorApproval" to "",
                        "note" to ""
                    )
                )
            )
        )

        val id = saved.id.toString()

        redisTemplate.opsForList().remove(DocumentQueueWorker.queueKey, 0, id)
        redisTemplate.opsForList().leftPush(DocumentQueueWorker.queueKey, id)

        return saved
    }

    fun downloadDocument(user: User, id: String): ByteArray {
        val document = getDocument(user, id)
        val outputStream = ByteArrayOutputStream()

        val objectKey = "hwp/${document.publicId}.hwp"

        s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(R2Config.privateBucket)
                .key(objectKey)
                .build()
        ).use {
            it.transferTo(outputStream)
        }

        return outputStream.toByteArray()
    }

    fun updateShared(user: User, id: String, shared: Boolean) {
        val saved = documentRepository.save(
            getDocument(user, id).copy(
                shared = shared,
            )
        )

        val id = saved.id.toString()

        redisTemplate.opsForList().remove(DocumentQueueWorker.queueKey, 0, id)
        redisTemplate.opsForList().leftPush(DocumentQueueWorker.queueKey, id)
    }
}
