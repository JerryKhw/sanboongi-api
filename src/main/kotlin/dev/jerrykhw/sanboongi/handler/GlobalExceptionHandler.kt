package dev.jerrykhw.sanboongi.handler

import dev.jerrykhw.sanboongi.model.DefaultResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<DefaultResponse> {
        val errorResponse = DefaultResponse(
            message = ex.message ?: "server_error"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(): ResponseEntity<DefaultResponse> {
        val errorResponse = DefaultResponse(
            message = "bad_request",
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
