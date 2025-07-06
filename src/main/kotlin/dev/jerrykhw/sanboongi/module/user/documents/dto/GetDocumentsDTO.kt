package dev.jerrykhw.sanboongi.module.user.documents.dto

import java.time.LocalDateTime

data class GetDocumentsData(
    val id: String,
    val title: String,
    val createdAt: LocalDateTime,
)
