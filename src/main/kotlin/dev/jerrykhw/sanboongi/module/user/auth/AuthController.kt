package dev.jerrykhw.sanboongi.module.user.auth

import dev.jerrykhw.sanboongi.model.DataResponse
import dev.jerrykhw.sanboongi.model.DefaultResponse
import dev.jerrykhw.sanboongi.model.TokenData
import dev.jerrykhw.sanboongi.module.user.auth.dto.SignInRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/sign")
    @Operation(summary = "로그인")
    fun signIn(@RequestBody request: SignInRequest): ResponseEntity<DataResponse<TokenData>> {
        val data = authService.signIn(request)

        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse("success", data))
    }

    @GetMapping("/sign")
    @Operation(summary = "로그인 확인")
    fun signCheck(): ResponseEntity<DefaultResponse> {

        return ResponseEntity.status(HttpStatus.OK).body(DefaultResponse("success"))
    }
}
