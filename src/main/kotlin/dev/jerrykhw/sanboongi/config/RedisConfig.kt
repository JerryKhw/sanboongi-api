package dev.jerrykhw.sanboongi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.prefix}") private val redisPrefix: String,
) {
    @Bean
    fun messageListenerAdapter(redisSubscriber: RedisSubscriber): MessageListenerAdapter {
        return MessageListenerAdapter(redisSubscriber, "onMessage")
    }

    @Bean
    fun redisMessageListenerContainer(
        connectionFactory: RedisConnectionFactory,
        listenerAdapter: MessageListenerAdapter
    ): RedisMessageListenerContainer {
        return RedisMessageListenerContainer().apply {
            setConnectionFactory(connectionFactory)
            addMessageListener(listenerAdapter, PatternTopic("$redisPrefix:topic:document.*"))
        }
    }
}

@Component
class RedisSubscriber(
    private val messagingTemplate: SimpMessagingTemplate,
    @Value("\${spring.data.redis.prefix}") private val redisPrefix: String,
) : MessageListener {
    override fun onMessage(message: Message, pattern: ByteArray?) {
        val fullChannel = String(message.channel)
        val payload = String(message.body)

        val channelWithoutPrefix = fullChannel.removePrefix("$redisPrefix:")
        val websocketDestination = "/" + channelWithoutPrefix.replace(':', '/')
        messagingTemplate.convertAndSend(websocketDestination, payload)
    }
}

@Component
class RedisPublisher(
    private val redisTemplate: RedisTemplate<String, String>,
    @Value("\${spring.data.redis.prefix}") private val redisPrefix: String,
) {
    fun publish(channel: String, message: String) {
        val fullChannel = "$redisPrefix:$channel"
        redisTemplate.convertAndSend(fullChannel, message)
    }
}
