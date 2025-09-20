package dev.jerrykhw.sanboongi.model

data class KakaoAccessTokenInfoResponse(
    val id: Long,
)

data class AppleKeysResponse(
    val keys: List<AppleKey>
)

data class AppleKey(
    val kty: String,
    val kid: String,
    val use: String,
    val alg: String,
    val n: String,
    val e: String
)