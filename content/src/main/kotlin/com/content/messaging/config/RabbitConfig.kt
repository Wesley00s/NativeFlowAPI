package com.content.messaging.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    companion object {
        const val VIDEO_TRANSCRIPTION_CMD = "video.transcription.cmd"
        const val VIDEO_TRANSCRIPTION_RESULT = "video.transcription.result"
        const val VIDEO_TRANSLATION_CMD = "video.translation.cmd"
        const val VIDEO_TRANSLATION_RESULT = "video.translation.result"
        const val VIDEO_SYNC_CMD = "video.sync.cmd"
        const val VIDEO_SYNC_RESULT = "video.sync.result"
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerKotlinModule()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return mapper
    }

    @Bean
    fun jsonMessageConverter(objectMapper: ObjectMapper): MessageConverter {
        return Jackson2JsonMessageConverter(objectMapper)
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, jsonMessageConverter: MessageConverter): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = jsonMessageConverter
        return template
    }

    @Bean
    fun rabbitListenerContainerFactory(connectionFactory: ConnectionFactory): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)

        return factory
    }

    @Bean
    fun transcriptionCmdQueue() = Queue(VIDEO_TRANSCRIPTION_CMD, true)
    @Bean
    fun transcriptionResultQueue() = Queue(VIDEO_TRANSCRIPTION_RESULT, true)
    @Bean
    fun translationCmdQueue() = Queue(VIDEO_TRANSLATION_CMD, true)
    @Bean
    fun translationResultQueue() = Queue(VIDEO_TRANSLATION_RESULT, true)
    @Bean
    fun syncCmdQueue() = Queue(VIDEO_SYNC_CMD, true)
    @Bean
    fun syncResultQueue() = Queue(VIDEO_SYNC_RESULT, true)
}