package dev.jerrykhw.sanboongi.module.user.users

import dev.jerrykhw.sanboongi.entity.User
import dev.jerrykhw.sanboongi.enums.SocialType
import dev.jerrykhw.sanboongi.module.user.users.dto.SignUpRequest
import dev.jerrykhw.sanboongi.module.user.users.dto.UpdateNicknameRequest
import dev.jerrykhw.sanboongi.repository.UserRepository
import dev.jerrykhw.sanboongi.util.nanoid.NanoId
import dev.jerrykhw.sanboongi.util.nanoid.PublicIdEntitySaver
import dev.jerrykhw.sanboongi.util.social.Social
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UsersService(
    private val social: Social,
    private val userRepository: UserRepository,
    private val publicIdEntitySaver: PublicIdEntitySaver
) {
    fun signUp(request: SignUpRequest): User {
        when (request.socialType) {
            SocialType.KAKAO -> {
                val id = social.getKakaoInfo(request.socialToken)

                var nickname: String

                do {
                    nickname = NanoId.generateNickname()
                } while (userRepository.findByNickname(nickname) != null)

                return publicIdEntitySaver.saveWithNanoIdRetry(
                    userRepository, User(
                        publicId = NanoId.generate(),
                        email = request.email,
                        nickname = nickname,
                        socialId = id,
                        socialType = request.socialType
                    )
                )
            }

            SocialType.APPLE -> {
                val id = social.getAppleInfo(request.socialToken)

                var nickname: String

                do {
                    nickname = NanoId.generateNickname()
                } while (userRepository.findByNickname(nickname) != null)

                return publicIdEntitySaver.saveWithNanoIdRetry(
                    userRepository, User(
                        publicId = NanoId.generate(),
                        email = request.email,
                        nickname = nickname,
                        socialId = id,
                        socialType = request.socialType
                    )
                )
            }
        }
    }

    fun leave(user: User) {
        userRepository.deleteById(user.id)
    }

    fun updateNickname(user: User, nickname: String) {
        userRepository.save(
            user.copy(
                nickname = nickname,
            )
        )
    }
}
