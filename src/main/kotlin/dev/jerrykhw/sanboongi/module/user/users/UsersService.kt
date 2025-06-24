package dev.jerrykhw.sanboongi.module.user.users


import dev.jerrykhw.sanboongi.entity.User
import dev.jerrykhw.sanboongi.enums.SocialType
import dev.jerrykhw.sanboongi.model.TokenData
import dev.jerrykhw.sanboongi.module.user.users.dto.SignUpRequest
import dev.jerrykhw.sanboongi.repository.UserRepository
import dev.jerrykhw.sanboongi.util.nanoid.NanoId
import dev.jerrykhw.sanboongi.util.nanoid.PublicIdEntitySaver
import dev.jerrykhw.sanboongi.util.social.Social
import org.springframework.stereotype.Service

@Service
class UsersService(
    private val userRepository: UserRepository,
    private val publicIdEntitySaver: PublicIdEntitySaver
) {
    fun signUp(request: SignUpRequest): TokenData {

        when(request.socialType) {
            SocialType.KAKAO -> {
                val id = Social.getKakaoInfo(request.socialToken)

                val user = User(
                    publicId = NanoId.generate(),
                    nickname = request.nickname,
                    socialId = id,
                    socialType = request.socialType
                )

                publicIdEntitySaver.saveWithNanoIdRetry(userRepository, user)
            }
            SocialType.APPLE -> TODO()
        }

        return TokenData(
            "",
            ""
        )
    }
}
