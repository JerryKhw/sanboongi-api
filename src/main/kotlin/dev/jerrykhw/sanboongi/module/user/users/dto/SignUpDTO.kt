package dev.jerrykhw.sanboongi.module.user.users.dto

import dev.jerrykhw.sanboongi.enums.SocialType

data class SignUpRequest(
    val socialType: SocialType,
    val socialToken: String,
    val email: String,
)
