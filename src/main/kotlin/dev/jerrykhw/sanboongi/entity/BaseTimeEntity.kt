package dev.jerrykhw.sanboongi.entity

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTimeEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "timestamptz")
    lateinit var createdAt: LocalDateTime
}