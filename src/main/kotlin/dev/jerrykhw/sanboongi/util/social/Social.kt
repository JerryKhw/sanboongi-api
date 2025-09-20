package dev.jerrykhw.sanboongi.util.social

import com.fasterxml.jackson.databind.ObjectMapper
import dev.jerrykhw.sanboongi.model.AppleKeysResponse
import dev.jerrykhw.sanboongi.model.KakaoAccessTokenInfoResponse
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.math.BigInteger
import java.net.URI
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class Social(
    @Value("\${social.apple.client-id}") private val appleClientId: String,
) {
    fun getKakaoInfo(socialToken: String): String {
        try {
            val restTemplate = RestTemplate()

            val uri = URI("https://kapi.kakao.com/v1/user/access_token_info")

            val headers = HttpHeaders().apply {
                setBearerAuth(socialToken)
            }

            val entity = HttpEntity<Void>(headers)

            val response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                KakaoAccessTokenInfoResponse::class.java
            )

            return response.body?.id?.toString() ?: throw IllegalStateException()
        } catch (e: Exception) {
            throw IllegalStateException("invalid_token")
        }
    }

    fun getAppleInfo(socialToken: String): String {
        try {
            val restTemplate = RestTemplate()

            val keysUri = URI("https://appleid.apple.com/auth/keys")
            val entity = HttpEntity<Void>(HttpHeaders())

            val response = restTemplate.exchange(
                keysUri,
                HttpMethod.GET,
                entity,
                AppleKeysResponse::class.java
            )

            val keysResponse = response.body ?: throw IllegalStateException("invalid_token")

            val tokenParts = socialToken.split(".")
            if (tokenParts.size != 3) {
                throw IllegalStateException("invalid_token")
            }

            val headerJson = String(Base64.getUrlDecoder().decode(tokenParts[0]))
            val objectMapper = ObjectMapper()
            val headerMap = objectMapper.readValue(headerJson, Map::class.java)

            val kid = headerMap["kid"] as? String
                ?: throw IllegalStateException("invalid_token")

            val matchingKey = keysResponse.keys.find { it.kid == kid }
                ?: throw IllegalStateException("invalid_token")

            val nBytes = Base64.getUrlDecoder().decode(matchingKey.n)
            val eBytes = Base64.getUrlDecoder().decode(matchingKey.e)
            val modulus = BigInteger(1, nBytes)
            val exponent = BigInteger(1, eBytes)
            val keySpec = RSAPublicKeySpec(modulus, exponent)
            val keyFactory = KeyFactory.getInstance("RSA")
            val publicKey = keyFactory.generatePublic(keySpec)

            val parser = Jwts.parser()
                .verifyWith(publicKey)
                .requireAudience(appleClientId)
                .requireIssuer("https://appleid.apple.com")
                .build()

            val claims = parser.parseSignedClaims(socialToken).payload

            return claims.subject ?: throw IllegalStateException("invalid_token")
        } catch (e: Exception) {
            throw IllegalStateException("invalid_token")
        }
    }
}
