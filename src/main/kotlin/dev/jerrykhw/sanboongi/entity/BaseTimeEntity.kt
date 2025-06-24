package dev.jerrykhw.sanboongi.entity

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import jakarta.persistence.*
import java.time.OffsetDateTime
import org.springframework.data.annotation.CreatedDate

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "timestamptz")
    lateinit var createdAt: OffsetDateTime
}