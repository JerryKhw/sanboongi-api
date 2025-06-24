package dev.jerrykhw.sanboongi.module.user.users

import dev.jerrykhw.sanboongi.model.DataResponse
import dev.jerrykhw.sanboongi.model.TokenData
import dev.jerrykhw.sanboongi.module.user.users.dto.SignUpRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Tag(name = "Users")
class UsersController(
    private val usersService: UsersService
) {
    @PostMapping
    @Operation(summary = "회원가입")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<DataResponse<TokenData>> {
        val data = usersService.signUp(request)

        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse("success", data))
    }
}
