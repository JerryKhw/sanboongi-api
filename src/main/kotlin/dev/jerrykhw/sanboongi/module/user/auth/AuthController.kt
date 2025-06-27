package dev.jerrykhw.sanboongi.module.user.auth

import dev.jerrykhw.sanboongi.annotation.CurrentUser
import dev.jerrykhw.sanboongi.entity.User
import dev.jerrykhw.sanboongi.model.DataResponse
import dev.jerrykhw.sanboongi.model.DefaultResponse
import dev.jerrykhw.sanboongi.module.user.auth.dto.SignCheckData
import dev.jerrykhw.sanboongi.module.user.auth.dto.SignInRequest
import dev.jerrykhw.sanboongi.util.jwt.Jwt
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
class AuthController(
    private val jwt: Jwt,
    private val authService: AuthService
) {
    @PostMapping("/sign")
    @Operation(summary = "로그인")
    fun signIn(@RequestBody request: SignInRequest, response: HttpServletResponse): ResponseEntity<DefaultResponse> {
        val user = authService.signIn(request)
        jwt.saveToken(user.publicId, response)
        return ResponseEntity.status(HttpStatus.CREATED).body(DefaultResponse("success"))
    }

    @PostMapping("/sign/new")
    @Operation(summary = "토큰 재발급")
    fun signNew(@CurrentUser user: User, response: HttpServletResponse): ResponseEntity<DefaultResponse> {
        jwt.saveToken(user.publicId, response)
        return ResponseEntity.status(HttpStatus.CREATED).body(DefaultResponse("success"))
    }

    @GetMapping("/sign")
    @Operation(summary = "로그인 확인")
    fun signCheck(@CurrentUser user: User): ResponseEntity<DataResponse<SignCheckData>> {
        return ResponseEntity.status(HttpStatus.OK).body(
            DataResponse(
                "success", SignCheckData(
                    nickname = user.nickname,
                    email = user.email
                )
            )
        )
    }

    @DeleteMapping("/sign")
    @Operation(summary = "로그아웃")
    fun signOut(response: HttpServletResponse): ResponseEntity<DefaultResponse> {
        jwt.resetToken(response)
        return ResponseEntity.status(HttpStatus.OK).body(
            DefaultResponse(
              "success"
            )
        )
    }
}
