package dev.jerrykhw.sanboongi.module.user.shared_documents

import dev.jerrykhw.sanboongi.entity.Document
import dev.jerrykhw.sanboongi.repository.DocumentRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException


@Service
class SharedDocumentsService(
    private val documentRepository: DocumentRepository,
) {
    fun getDocument(id: String): Document {
        val document = documentRepository.findBySharedTrueAndPublicId(id)

        if (document == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "not_found")
        }

        return document
    }
}
