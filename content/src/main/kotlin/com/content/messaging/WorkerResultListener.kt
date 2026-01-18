package com.content.messaging

import com.content.api.dto.messaging.GlossaryResultPayload
import com.content.api.dto.messaging.TranscriptionResultPayload
import com.content.api.dto.messaging.TranslationResultPayload
import com.content.messaging.config.RabbitConfig
import com.content.service.VideoService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class WorkerResultListener(
    private val videoService: VideoService,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(WorkerResultListener::class.java)

    @RabbitListener(queues = [RabbitConfig.VIDEO_TRANSCRIPTION_RESULT])
    fun handleTranscriptionResult(jsonPayload: String) {
        logger.debug("Received Transcription Payload: {}", jsonPayload)

        try {
            val payload = objectMapper.readValue(jsonPayload, TranscriptionResultPayload::class.java)

            if (payload.status == "SUCCESS") {
                videoService.handleTranscriptionSuccess(payload)
            } else {
                logger.error("Worker reported Transcription Error: {}", payload.errorMessage)
            }
        } catch (e: Exception) {
            logger.error("Failed to process Transcription message", e)
        }
    }

    @RabbitListener(queues = [RabbitConfig.VIDEO_TRANSLATION_RESULT])
    fun handleTranslationResult(jsonPayload: String) {
        logger.debug("Received Translation Payload: {}", jsonPayload)

        try {
            val payload = objectMapper.readValue(jsonPayload, TranslationResultPayload::class.java)

            if (payload.status == "SUCCESS") {
                videoService.handleTranslationSuccess(payload)
            } else {
                logger.error("Worker reported Translation Error: {}", payload.errorMessage)
            }
        } catch (e: Exception) {
            logger.error("Failed to process Translation message", e)
        }
    }

    @RabbitListener(queues = [RabbitConfig.VIDEO_GLOSSARY_RESULT])
    fun handleGlossaryResult(jsonPayload: String) {
        logger.debug("Received Glossary Payload: {}", jsonPayload)

        try {
            val payload = objectMapper.readValue(jsonPayload, GlossaryResultPayload::class.java)

            videoService.handleGlossarySuccess(payload)

        } catch (e: Exception) {
            logger.error("Failed to process Glossary message", e)
        }
    }
}