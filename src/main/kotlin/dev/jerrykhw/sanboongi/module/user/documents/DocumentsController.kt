package dev.jerrykhw.sanboongi.module.user.documents

import dev.jerrykhw.sanboongi.annotation.CurrentUser
import dev.jerrykhw.sanboongi.entity.User
import dev.jerrykhw.sanboongi.model.DataResponse
import dev.jerrykhw.sanboongi.model.DefaultResponse
import dev.jerrykhw.sanboongi.module.user.documents.dto.GetDocumentData
import dev.jerrykhw.sanboongi.module.user.documents.dto.GetDocumentsData
import dev.jerrykhw.sanboongi.module.user.documents.dto.UpdateDocumentRequest
import dev.jerrykhw.sanboongi.module.user.documents.dto.UpdateSharedRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/documents")
@Tag(name = "Documents")
class DocumentsController(
    private val documentsService: DocumentsService
) {
    @GetMapping
    @Operation(summary = "문서 조회")
    fun getDocuments(@CurrentUser user: User): ResponseEntity<DataResponse<List<GetDocumentsData>>> {
        val documents = documentsService.getDocuments(user)
        return ResponseEntity.status(HttpStatus.OK).body(DataResponse("success", documents.map {
            GetDocumentsData(
                id = it.publicId,
                title = it.title,
                createdAt = it.createdAt
            )
        }))
    }

    @GetMapping("/{id}")
    @Operation(summary = "문서 상세")
    fun getDocument(
        @CurrentUser user: User,
        @PathVariable("id") id: String,
    ): ResponseEntity<DataResponse<GetDocumentData>> {
        val document = documentsService.getDocument(user, id)

        return ResponseEntity.status(HttpStatus.OK).body(
            DataResponse(
                "success", GetDocumentData(
                    id = document.publicId,
                    title = document.title,
                    affiliation = document.affiliation,
                    enrollmentDate = document.enrollmentDate,
                    name = document.name,
                    birthDate = document.birthDate,
                    verifier = document.verifier,
                    records = document.records,
                    updatedAt = document.updatedAt,
                    createdAt = document.createdAt,
                    shared = document.shared,
                    isDownloadReady = document.updatedAt == document.downloadFileUploadedAt
                )
            )
        )
    }

    @PutMapping("/{id}")
    @Operation(summary = "문서 업데이트")
    fun updateDocument(
        @CurrentUser user: User,
        @PathVariable("id") id: String,
        @RequestBody request: UpdateDocumentRequest
    ): ResponseEntity<DataResponse<LocalDateTime>> {
        val document = documentsService.updateDocument(user, id, request)

        return ResponseEntity.status(HttpStatus.OK).body(
            DataResponse(
                "success", document.updatedAt
            )
        )
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "문서 삭제")
    fun deleteDocument(
        @CurrentUser user: User,
        @PathVariable("id") id: String,
    ): ResponseEntity<DefaultResponse> {
        documentsService.deleteDocument(user, id)

        return ResponseEntity.status(HttpStatus.OK).body(
            DefaultResponse(
                "success",
            )
        )
    }

    @PostMapping
    @Operation(summary = "새 문서")
    fun newDocument(@CurrentUser user: User): ResponseEntity<DataResponse<String>> {
        val document = documentsService.newDocument(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse("success", document.publicId))
    }

    @PostMapping("/{id}/download")
    @Operation(
        summary = "문서 다운로드",
        responses = [
            ApiResponse(
                responseCode = "200",
                content = [Content(
                    mediaType = "application/x-hwp",
                    schema = Schema(type = "string", format = "binary")
                )]
            )
        ]
    )
    fun downloadDocument(
        @CurrentUser user: User,
        @PathVariable("id") id: String,
    ): ResponseEntity<ByteArray> {
        val hwpBytes = documentsService.downloadDocument(user, id)
        val headers = HttpHeaders()
        headers.contentType = MediaType.valueOf("application/x-hwp")
        headers.setContentDispositionFormData("attachment", "document.hwp")
        return ResponseEntity(hwpBytes, headers, HttpStatus.OK)
    }

    @PatchMapping("/{id}/shared")
    @Operation(summary = "문서 공유 상태 변경")
    fun updateShared(
        @CurrentUser user: User,
        @PathVariable id: String,
        @RequestBody request: UpdateSharedRequest
    ): ResponseEntity<DefaultResponse> {
        documentsService.updateShared(user, id, request.shared)
        return ResponseEntity.status(HttpStatus.OK).body(
            DefaultResponse(
                "success"
            )
        )
    }
}
