package dev.jerrykhw.sanboongi.entity

import dev.jerrykhw.sanboongi.enums.SocialType
import dev.jerrykhw.sanboongi.util.nanoid.PublicIdEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    override var publicId: String = "",

    @Column(nullable = false, length = 100)
    val email: String = "",

    @Column(nullable = false, length = 100)
    val nickname: String = "",

    @Column(nullable = false)
    val socialId: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val socialType: SocialType = SocialType.KAKAO,
) : PublicIdEntity, BaseTimeEntity()
