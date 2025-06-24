package dev.jerrykhw.sanboongi.module.user.auth

import dev.jerrykhw.sanboongi.model.TokenData
import dev.jerrykhw.sanboongi.module.user.auth.dto.SignInRequest
import dev.jerrykhw.sanboongi.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository
) {
    fun signIn(request: SignInRequest): TokenData {
        val user = userRepository.findById(0)
            .orElseThrow { NoSuchElementException("not_found") }

        return TokenData(
            "",
            ""
        )
    }
}
