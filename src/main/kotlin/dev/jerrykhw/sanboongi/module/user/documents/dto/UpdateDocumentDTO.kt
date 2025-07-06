package dev.jerrykhw.sanboongi.module.user.documents.dto

import java.time.LocalDate

data class UpdateDocumentRequest(
    val title: String,
    val affiliation: String,
    val enrollmentDate: LocalDate,
    val name: String,
    val birthDate: LocalDate,
    val verifier: String,
    val records: List<Map<String, Any>>,
)
