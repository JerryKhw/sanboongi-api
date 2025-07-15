package dev.jerrykhw.sanboongi.module.user.shared_documents

import dev.jerrykhw.sanboongi.model.DataResponse
import dev.jerrykhw.sanboongi.module.user.shared_documents.dto.GetSharedDocumentData
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/shared-documents")
@Tag(name = "Shared Documents")
class SharedDocumentsController(
    private val sharedDocumentsService: SharedDocumentsService,
    @Value("\${r2.public-url}") private val r2PublicUrl: String,
) {
    @GetMapping("/{id}")
    @Operation(summary = "문서 상세")
    fun getSharedDocument(@PathVariable("id") id: String): ResponseEntity<DataResponse<GetSharedDocumentData>> {
        val document = sharedDocumentsService.getDocument(id)

        return ResponseEntity.status(HttpStatus.OK).body(
            DataResponse(
                "success", GetSharedDocumentData(
                    id = document.publicId,
                    title = document.title,
                    downloadUrl = "${r2PublicUrl}/hwp/${document.publicId}.hwp",
                    isDownloadReady = document.updatedAt == document.shareFileUploadedAt,
                )
            )
        )
    }
}
