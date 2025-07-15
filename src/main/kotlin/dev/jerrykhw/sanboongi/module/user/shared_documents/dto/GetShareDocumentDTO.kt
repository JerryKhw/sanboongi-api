package dev.jerrykhw.sanboongi.module.user.shared_documents.dto

data class GetSharedDocumentData(
    val id: String,
    val title: String,
    val downloadUrl: String,
    val isDownloadReady: Boolean,
)