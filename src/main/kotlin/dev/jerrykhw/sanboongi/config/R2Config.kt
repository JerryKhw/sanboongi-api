package dev.jerrykhw.sanboongi.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
class R2Config(
    @Value("\${r2.url}") private val r2Url: String,
    @Value("\${r2.access-id}") private val r2AccessId: String,
    @Value("\${r2.access-secret}") private val r2AccessSecret: String,
    @Value("\${r2.bucket.public}") private val r2PublicBucket: String,
    @Value("\${r2.bucket.private}") private val r2PrivateBucket: String,
) {
    companion object {
        lateinit var publicBucket: String
        lateinit var privateBucket: String
    }

    @PostConstruct
    fun init() {
        publicBucket = r2PublicBucket
        privateBucket = r2PrivateBucket
    }

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of("auto"))
            .endpointOverride(URI(r2Url))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(r2AccessId, r2AccessSecret)
                )
            )
            .build()
    }
}
