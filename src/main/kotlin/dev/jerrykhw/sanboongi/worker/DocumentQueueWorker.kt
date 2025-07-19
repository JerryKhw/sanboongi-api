package dev.jerrykhw.sanboongi.worker

import dev.jerrykhw.sanboongi.config.R2Config
import dev.jerrykhw.sanboongi.config.RedisPublisher
import dev.jerrykhw.sanboongi.entity.Document
import dev.jerrykhw.sanboongi.repository.DocumentRepository
import dev.jerrykhw.sanboongi.util.hwp.Hwp.getTablesFromSection
import dev.jerrykhw.sanboongi.util.hwp.Hwp.setCellTextByField
import jakarta.annotation.PostConstruct
import kr.dogfoot.hwplib.`object`.bodytext.control.ControlSectionDefine
import kr.dogfoot.hwplib.`object`.bodytext.control.ControlType
import kr.dogfoot.hwplib.`object`.bodytext.paragraph.Paragraph
import kr.dogfoot.hwplib.reader.HWPReader
import kr.dogfoot.hwplib.tool.paragraphadder.ParagraphAdder
import kr.dogfoot.hwplib.tool.paragraphadder.control.SectionDefineCopier
import kr.dogfoot.hwplib.writer.HWPWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CopyObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Arrays


@Service
class DocumentQueueWorker(
    private val redisTemplate: StringRedisTemplate,
    private val redisPublisher: RedisPublisher,
    private val documentRepository: DocumentRepository,
    private val s3Client: S3Client,
    @Value("\${spring.data.redis.prefix}") private val redisPrefix: String,
) {
    companion object {
        lateinit var queueKey: String
    }

    @PostConstruct
    fun init() {
        queueKey = "${redisPrefix}:document:generate:queue"
    }

    @PostConstruct
    fun startWorker() {
        Thread {
            while (true) {
                try {
                    val documentId = redisTemplate.opsForList()
                        .leftPop("$redisPrefix:document:generate:queue", Duration.ofSeconds(10))

                    if (documentId != null) {
                        println("Processing document immediately: $documentId")
                        saveDocument(documentId.toLong())
                    }
                } catch (ex: Exception) {
                    println("Error in queue processing: ${ex.message}")
                    Thread.sleep(1000)
                }
            }
        }.start()
    }

    private fun saveDocument(documentId: Long) {
        val document = documentRepository.findByIdOrNull(documentId)

        if (document == null) {
            return
        }

        val objectKey = "hwp/${document.publicId}.hwp"

        var newDocument: Document = document
        var isUpdate = false

        if (document.updatedAt != document.downloadFileUploadedAt) {
            val file = HWPReader.fromFile("src/main/resources/base.hwp")
            val newFile = file.clone(false)

            val records = document.records
            val page = (records.size - 1) / 19

            val section = file.bodyText.sectionList.first()

            for (index in 0..page) {
                val currentSection = if (index == 0) {
                    newFile.bodyText.sectionList.first()
                } else {
                    newFile.bodyText.sectionList.add(section)
                    newFile.bodyText.sectionList.elementAt(index)
                }

                val start = index * 19
                val endExclusive = minOf((index + 1) * 19, records.size)
                val columns = records.subList(start, endExclusive)

                val table = getTablesFromSection(currentSection).firstOrNull()

                table?.let {
                    setCellTextByField(it, "소속", " 소속  ${document.affiliation}")
                    setCellTextByField(
                        it, "편입일자",
                        " 편입일자  ${document.enrollmentDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}"
                    )
                    setCellTextByField(it, "성명", " 성명  ${document.name}")
                    setCellTextByField(
                        it, "생년월일",
                        " 생년월일  ${document.birthDate.format(DateTimeFormatter.ofPattern("yyMMdd"))}"
                    )
                    setCellTextByField(it, "확인자", " 확인자  ${document.verifier}\n                           (서명 또는 인)")

                    columns.forEachIndexed { idx, record ->
                        val fieldIndex = idx + 1

                        setCellTextByField(it, "구분$fieldIndex", record["type"].orEmpty())

                        val startDate = record["startDate"]?.replace("-", ".").orEmpty()
                        val startTime = record["startTime"].orEmpty()
                        val startText = if (startTime.isNotBlank()) "$startDate\n($startTime)" else startDate
                        setCellTextByField(it, "부터$fieldIndex", startText)

                        val endDate = record["endDate"]?.replace("-", ".").orEmpty()
                        val endTime = record["endTime"].orEmpty()
                        val endText = if (endTime.isNotBlank()) "$endDate\n($endTime)" else endDate
                        setCellTextByField(it, "까지$fieldIndex", endText)

                        setCellTextByField(it, "일수$fieldIndex", record["duration"].orEmpty())
                        setCellTextByField(it, "목적$fieldIndex", record["reason"].orEmpty())
                        setCellTextByField(it, "장소$fieldIndex", record["location"].orEmpty())
                        setCellTextByField(it, "누계$fieldIndex", record["accumulated"].orEmpty())
                        setCellTextByField(it, "담당$fieldIndex", record["managerApproval"].orEmpty())
                        setCellTextByField(it, "업체장$fieldIndex", record["supervisorApproval"].orEmpty())
                        setCellTextByField(it, "비고$fieldIndex", record["note"].orEmpty())
                    }
                }
            }

            val outputStream = ByteArrayOutputStream()
            HWPWriter.toStream(newFile, outputStream)

            val inputStream = ByteArrayInputStream(outputStream.toByteArray())

            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(R2Config.privateBucket)
                    .key(objectKey)
                    .contentType("application/x-hwp")
                    .build(),
                RequestBody.fromInputStream(inputStream, outputStream.size().toLong())
            )

            newDocument = newDocument.copy(
                downloadFileUploadedAt = document.updatedAt
            )
            isUpdate = true
        }

        if (document.shared && document.updatedAt != document.shareFileUploadedAt) {
            s3Client.copyObject(
                CopyObjectRequest.builder()
                    .sourceBucket(R2Config.privateBucket)
                    .sourceKey(objectKey)
                    .destinationBucket(R2Config.publicBucket)
                    .destinationKey(objectKey)
                    .build()
            )
            newDocument = newDocument.copy(
                shareFileUploadedAt = document.updatedAt
            )
            isUpdate = true
        } else if (document.shareFileUploadedAt != null) {
            s3Client.deleteObject(
                DeleteObjectRequest.builder()
                    .bucket(R2Config.publicBucket)
                    .key(objectKey)
                    .build(),
            )
            newDocument = newDocument.copy(
                shareFileUploadedAt = null
            )
            isUpdate = true
        }

        if (isUpdate) {
            documentRepository.save(
                newDocument
            )
            val channel = "topic:document.${document.publicId}"
            redisPublisher.publish(channel, "refresh")
        }
    }
}
