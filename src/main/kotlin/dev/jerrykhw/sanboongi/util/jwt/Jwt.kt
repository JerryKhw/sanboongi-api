package dev.jerrykhw.sanboongi.util.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import javax.crypto.SecretKey

@Component
class Jwt(
    @Value("\${app.mode}") private val appMode: String,
    @Value("\${jwt.access.secret}") private val accessSecret: String,
    @Value("\${jwt.access.expiration}") private val accessExpiration: Duration,
    @Value("\${jwt.refresh.secret}") private val refreshSecret: String,
    @Value("\${jwt.refresh.expiration}") private val refreshExpiration: Duration
) {
    private val isSecure: Boolean
        get() = appMode != "local"

    private val domain: String?
        get() = if(appMode != "local") "jerrykhw.dev" else null

    private val sameSite: String
        get() = if(appMode != "local") "None" else "Lax"


    enum class TokenType { ACCESS, REFRESH }

    private val accessSecretKey = Keys.hmacShaKeyFor(accessSecret.toByteArray())
    private val refreshSecretKey = Keys.hmacShaKeyFor(refreshSecret.toByteArray())

    private fun resolveKey(type: TokenType): SecretKey? {
        return when (type) {
            TokenType.ACCESS -> accessSecretKey
            TokenType.REFRESH -> refreshSecretKey
        }
    }

    fun saveToken(publicId: String, response: HttpServletResponse) {
        val accessToken = generateToken(publicId, TokenType.ACCESS)
        val refreshToken = generateToken(publicId, TokenType.REFRESH)

        val accessCookie: ResponseCookie = ResponseCookie.from("sb_access_token", accessToken)
            .path("/")
            .sameSite(sameSite)
            .httpOnly(true)
            .secure(isSecure)
            .maxAge(accessExpiration.seconds)
            .domain(domain)
            .build()

        val refreshCookie: ResponseCookie = ResponseCookie.from("sb_refresh_token", refreshToken)
            .path("/")
            .sameSite(sameSite)
            .httpOnly(true)
            .secure(isSecure)
            .maxAge(refreshExpiration.seconds)
            .domain(domain)
            .build()

        response.addHeader("Set-Cookie", accessCookie.toString())
        response.addHeader("Set-Cookie", refreshCookie.toString())
    }

    fun resetToken(response: HttpServletResponse) {
        val accessCookie: ResponseCookie = ResponseCookie.from("sb_access_token", "")
            .path("/")
            .sameSite(sameSite)
            .httpOnly(true)
            .secure(isSecure)
            .maxAge(0)
            .domain(domain)
            .build()

        val refreshCookie: ResponseCookie = ResponseCookie.from("sb_refresh_token", "")
            .path("/")
            .sameSite(sameSite)
            .httpOnly(true)
            .secure(isSecure)
            .maxAge(0)
            .domain(domain)
            .build()

        response.addHeader("Set-Cookie", accessCookie.toString())
        response.addHeader("Set-Cookie", refreshCookie.toString())
    }

    fun generateToken(publicId: String, type: TokenType): String {
        val now = Instant.now()
        val expiry = now.plus(
            when (type) {
                TokenType.ACCESS -> accessExpiration
                TokenType.REFRESH -> refreshExpiration
            }
        )
        val key = resolveKey(type)

        return Jwts.builder()
            .claim("sub", publicId)
            .claim("iat", now.epochSecond)
            .claim("exp", expiry.epochSecond)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String, type: TokenType): Boolean {
        val key = resolveKey(type)
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (ex: Exception) {
            false
        }
    }

    fun getPublicIdFromToken(token: String, type: TokenType): String {
        val key = resolveKey(type)
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload()
        return claims["sub"] as String
    }

    fun getPublicIdFromRequest(request: HttpServletRequest, type: TokenType): String? {
        val cookies = request.cookies ?: return null
        val tokenName = when (type) {
            TokenType.ACCESS -> "sb_access_token"
            TokenType.REFRESH -> "sb_refresh_token"
        }

        val token = cookies.firstOrNull { it.name == tokenName }?.value ?: return null

        return try {
            getPublicIdFromToken(token, type)
        } catch (e: Exception) {
            null
        }
    }
}
