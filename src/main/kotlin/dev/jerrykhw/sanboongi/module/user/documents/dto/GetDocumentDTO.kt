package dev.jerrykhw.sanboongi.module.user.documents.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class GetDocumentData(
    val id: String,
    val title: String,
    val affiliation: String,
    val enrollmentDate: LocalDate,
    val name: String,
    val birthDate: LocalDate,
    val verifier: String,
    val records: List<Map<String, Any>>,
    val updatedAt: LocalDateTime,
    val createdAt: LocalDateTime,
)
