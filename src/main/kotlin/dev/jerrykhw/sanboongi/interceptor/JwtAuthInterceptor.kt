package dev.jerrykhw.sanboongi.interceptor

import dev.jerrykhw.sanboongi.repository.UserRepository
import dev.jerrykhw.sanboongi.util.jwt.Jwt
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtAuthInterceptor(
    private val jwt: Jwt,
    private val userRepository: UserRepository
) : HandlerInterceptor {

    private val excludePathsWithMethods = listOf(
        Pair("/auth/sign", "POST"),
        Pair("/auth/sign", "DELETE"),
        Pair("/users", "POST"),
        Pair("/shared-documents/**", "GET"),
        Pair("/swagger-ui/**", "GET"),
        Pair("/v3/api-docs/**", "GET"),
        Pair("/swagger-ui.html", "GET")
    )

    private fun isExcluded(path: String, method: String): Boolean {
        return excludePathsWithMethods.any { (pattern, httpMethod) ->
            val pathMatches = if (pattern.endsWith("/**")) {
                path.startsWith(pattern.removeSuffix("/**"))
            } else {
                path == pattern
            }
            pathMatches && method.equals(httpMethod, ignoreCase = true)
        }
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val path = request.requestURI
        val method = request.method

        if (method.equals("OPTIONS", ignoreCase = true) || isExcluded(path, method)) {
            return true
        }

        return if (path == "/auth/sign/new" && method.equals("POST", ignoreCase = true)) {
            validateToken(request, response, Jwt.TokenType.REFRESH)
        } else {
            validateToken(request, response, Jwt.TokenType.ACCESS)
        }
    }

    private fun validateToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
        tokenType: Jwt.TokenType
    ): Boolean {
        val publicId = jwt.getPublicIdFromRequest(request, tokenType)
        if (publicId == null) {
            writeUnauthorized(response, "invalid_${tokenType.name.lowercase()}_token")
            return false
        }

        val user = userRepository.findByPublicId(publicId)
        if (user == null) {
            writeUnauthorized(response, "invalid_${tokenType.name.lowercase()}_token")
            return false
        }

        request.setAttribute("user", user)
        return true
    }

    private fun writeUnauthorized(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write("""{"message": "$message"}""".trimIndent())
    }
}
