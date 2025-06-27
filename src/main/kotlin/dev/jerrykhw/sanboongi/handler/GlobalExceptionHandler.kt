package dev.jerrykhw.sanboongi.handler

import dev.jerrykhw.sanboongi.model.DefaultResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<DefaultResponse> {
        if (ex is NoResourceFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                DefaultResponse("not_found")
            )
        }

        val errorResponse = DefaultResponse(
            message = ex.message ?: "server_error"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<DefaultResponse> {
        val errorResponse = DefaultResponse(
            message = ex.reason ?: "server_error"
        )
        return ResponseEntity.status(ex.statusCode).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(): ResponseEntity<DefaultResponse> {
        val errorResponse = DefaultResponse(
            message = "bad_request",
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
