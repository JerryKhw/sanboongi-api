package dev.jerrykhw.sanboongi.module.user.auth.dto

import dev.jerrykhw.sanboongi.enums.SocialType

data class SignInRequest(
    val socialType: SocialType,
    val socialToken: String,
)
