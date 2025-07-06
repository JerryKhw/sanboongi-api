package dev.jerrykhw.sanboongi.module.user.auth

import dev.jerrykhw.sanboongi.entity.User
import dev.jerrykhw.sanboongi.enums.SocialType
import dev.jerrykhw.sanboongi.module.user.auth.dto.SignInRequest
import dev.jerrykhw.sanboongi.repository.UserRepository
import dev.jerrykhw.sanboongi.util.social.Social
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val userRepository: UserRepository,
) {
    fun signIn(request: SignInRequest): User {
        when (request.socialType) {
            SocialType.KAKAO -> {
                val id = Social.getKakaoInfo(request.socialToken)

                val user = userRepository.findBySocialIdAndSocialType(
                    socialId = id,
                    socialType = request.socialType,
                )

                if (user == null) {
                    throw ResponseStatusException(HttpStatus.NOT_FOUND, "not_found")
                }

                return user
            }

            SocialType.APPLE -> TODO()
        }
    }
}
