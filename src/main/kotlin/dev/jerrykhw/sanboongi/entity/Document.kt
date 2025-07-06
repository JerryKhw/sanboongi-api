package dev.jerrykhw.sanboongi.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import dev.jerrykhw.sanboongi.util.nanoid.PublicIdEntity
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    override var publicId: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false, length = 20)
    val title: String = "개인별 복무상황부",

    @Column(nullable = false, length = 20)
    val affiliation: String = "",

    @Column(nullable = false, columnDefinition = "date")
    val enrollmentDate: LocalDate = LocalDate.now(),

    @Column(nullable = false, length = 20)
    val name: String = "",

    @Column(nullable = false, columnDefinition = "date")
    val birthDate: LocalDate = LocalDate.now(),

    @Column(nullable = false, length = 20)
    val verifier: String = "",

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    val records: List<Map<String, Any>> = emptyList(),

    @Column(nullable = false, columnDefinition = "timestamptz")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) : PublicIdEntity, BaseTimeEntity() {
    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}