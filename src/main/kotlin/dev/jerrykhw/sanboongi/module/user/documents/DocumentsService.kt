package dev.jerrykhw.sanboongi.module.user.documents

import dev.jerrykhw.sanboongi.entity.Document
import dev.jerrykhw.sanboongi.entity.User
import dev.jerrykhw.sanboongi.module.user.documents.dto.UpdateDocumentRequest
import dev.jerrykhw.sanboongi.repository.DocumentRepository
import dev.jerrykhw.sanboongi.util.nanoid.NanoId
import dev.jerrykhw.sanboongi.util.nanoid.PublicIdEntitySaver
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class DocumentsService(
    private val documentRepository: DocumentRepository,
    private val publicIdEntitySaver: PublicIdEntitySaver
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
        return documentRepository.save(
            getDocument(user, id).copy(
                title = request.title,
                affiliation = request.affiliation,
                enrollmentDate = request.enrollmentDate,
                name = request.name,
                birthDate = request.birthDate,
                verifier = request.verifier,
                records = request.records,
            )
        )
    }

    fun deleteDocument(user: User, id: String) {
        documentRepository.delete(
            getDocument(user, id)
        )
    }

    fun newDocument(user: User): Document {
        return publicIdEntitySaver.saveWithNanoIdRetry(
            documentRepository, Document(
                publicId = NanoId.generate(),
                user = user,
            )
        )
    }
}
