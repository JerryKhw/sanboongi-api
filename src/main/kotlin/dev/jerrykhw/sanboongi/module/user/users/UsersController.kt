package dev.jerrykhw.sanboongi.module.user.users

import dev.jerrykhw.sanboongi.annotation.CurrentUser
import dev.jerrykhw.sanboongi.entity.User
import dev.jerrykhw.sanboongi.model.DefaultResponse
import dev.jerrykhw.sanboongi.module.user.users.dto.SignUpRequest
import dev.jerrykhw.sanboongi.module.user.users.dto.UpdateNicknameRequest
import dev.jerrykhw.sanboongi.util.jwt.Jwt
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Tag(name = "Users")
class UsersController(
    private val jwt: Jwt,
    private val usersService: UsersService
) {
    @PostMapping
    @Operation(summary = "회원가입")
    fun signUp(@RequestBody request: SignUpRequest, response: HttpServletResponse): ResponseEntity<DefaultResponse> {
        val user = usersService.signUp(request)
        jwt.saveToken(user.publicId, response)
        return ResponseEntity.status(HttpStatus.CREATED).body(DefaultResponse("success"))
    }

    @DeleteMapping()
    @Operation(summary = "회원탈퇴")
    fun leave(@CurrentUser user: User, response: HttpServletResponse): ResponseEntity<DefaultResponse> {
        usersService.leave(user)
        jwt.resetToken(response)
        return ResponseEntity.status(HttpStatus.OK).body(DefaultResponse("success"))
    }

    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 변경")
    fun updateNickname(
        @CurrentUser user: User,
        @RequestBody request: UpdateNicknameRequest
    ): ResponseEntity<DefaultResponse> {
        usersService.updateNickname(user, request)
        return ResponseEntity.status(HttpStatus.OK).body(DefaultResponse("success"))
    }
}
