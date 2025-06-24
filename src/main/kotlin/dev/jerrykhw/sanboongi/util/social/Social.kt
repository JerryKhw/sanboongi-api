package dev.jerrykhw.sanboongi.util.social

import dev.jerrykhw.sanboongi.model.KakaoAccessTokenInfoResponse
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.net.URI

object Social {

    fun getKakaoInfo(socialToken: String): String {
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

        return response.body?.id?.toString() ?: throw IllegalStateException("invalid_token")
    }
}
