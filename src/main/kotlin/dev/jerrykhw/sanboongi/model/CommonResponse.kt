package dev.jerrykhw.sanboongi.model

data class DefaultResponse(
    val message: String,
)

data class DataResponse<T>(
    val message: String,
    val data: T
)