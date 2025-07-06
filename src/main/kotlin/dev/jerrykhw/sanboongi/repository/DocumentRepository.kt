package dev.jerrykhw.sanboongi.repository

import dev.jerrykhw.sanboongi.entity.Document
import org.springframework.data.jpa.repository.JpaRepository

interface DocumentRepository : JpaRepository<Document, Long> {
    fun findAllByUserIdOrderByUpdatedAtDesc(userId: Long): List<Document>

    fun findByUserIdAndPublicId(userId: Long, publicId: String): Document?

    fun deleteAllByUserId(userId: Long)
}