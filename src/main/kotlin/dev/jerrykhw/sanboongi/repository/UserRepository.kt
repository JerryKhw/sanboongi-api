package dev.jerrykhw.sanboongi.repository

import dev.jerrykhw.sanboongi.entity.User
import dev.jerrykhw.sanboongi.enums.SocialType
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findBySocialIdAndSocialType(socialId: String, socialType: SocialType): User?

    fun findByNickname(nickname: String): User?

    fun findByPublicId(publicId: String): User?
}