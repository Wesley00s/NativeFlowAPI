package com.content.service

import com.content.api.v1.dto.VideoTaskRequest
import com.content.api.v1.dto.messaging.*
import com.content.domain.enums.VideoStatus
import com.content.domain.model.SourceData
import com.content.domain.model.SubtitleItem
import com.content.domain.model.TranscriptionSegment
import com.content.domain.model.TranslationData
import com.content.domain.model.Video
import com.content.messaging.config.RabbitConfig
import com.content.repository.VideoRepository
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class VideoService(
    private val videoRepository: VideoRepository,
    private val rabbitTemplate: RabbitTemplate
) {
    private val logger = LoggerFactory.getLogger(VideoService::class.java)

    @Transactional
    fun initiateProcessing(request: VideoTaskRequest) {
        val videoOpt = videoRepository.findBySourceId(request.videoId)

        if (videoOpt.isPresent) {
            val video = videoOpt.get()

            if (video.sourceData != null) {
                logger.info("Video ${request.videoId} already transcribed. Skipping to Translation/Sync.")

                val transcriptionPayload = video.sourceData.transcription.map {
                    SubtitleItemPayload(it.text, it.start, it.end, it.conf)
                }

                val command = TranslationCommandPayload(
                    videoId = request.videoId,
                    originalText = video.sourceData.fullText,
                    sourceLang = video.sourceData.language,
                    targetLang = request.translationLanguage,
                    transcription = transcriptionPayload
                )

                val updatingVideo = video.copy(status = VideoStatus.TRANSLATING)
                videoRepository.save(updatingVideo)

                rabbitTemplate.convertAndSend(RabbitConfig.VIDEO_TRANSLATION_CMD, command)
                return
            }
        }
        logger.info("New video or no transcription found. Sending to Transcriber.")
        rabbitTemplate.convertAndSend(RabbitConfig.VIDEO_TRANSCRIPTION_CMD, request)
    }

    @Transactional
    fun handleTranscriptionSuccess(payload: TranscriptionResultPayload) {
        logger.info("Processing transcription success for videoId: {}", payload.videoId)

        val existingVideo = videoRepository.findBySourceId(payload.videoId).orElse(null)

        val videoToUpdate = if (existingVideo != null) {
            existingVideo
        } else {
            logger.warn("Video not found in DB. Creating new entry automatically for ID: {}", payload.videoId)
            
            Video(
                sourceId = payload.videoId,
                status = VideoStatus.PENDING,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                translations = emptyList()
            )
        }

        val targetLang = payload.targetLang

        val command = TranslationCommandPayload(
            videoId = payload.videoId,
            originalText = payload.sourceData.fullText,
            sourceLang = payload.sourceData.language,
            targetLang = targetLang,
            transcription = payload.sourceData.transcription
        )

        val finalVideoState = videoToUpdate.copy(
            status = VideoStatus.TRANSLATING, 
            durationSeconds = payload.duration.toLong(),
            updatedAt = Instant.now(),
            title = payload.sourceData.title ?: videoToUpdate.title,
            author = payload.sourceData.author,
            viewCount = payload.sourceData.viewCount,
            thumbnailUrl = payload.sourceData.thumbnailUrl,

            
            sourceData = SourceData(
                language = payload.sourceData.language,
                fullText = payload.sourceData.fullText,
                transcription = payload.sourceData.transcription.map {
                    TranscriptionSegment(it.text, it.start, it.end, it.conf)
                }
            )
        )

        videoRepository.save(finalVideoState)
        logger.info("Video saved and updated. Status: TRANSLATING. ID: {}", finalVideoState.sourceId)

        logger.info("Dispatching to Translation Queue (Target: {}).", targetLang)
        rabbitTemplate.convertAndSend(RabbitConfig.VIDEO_TRANSLATION_CMD, command)
    }

    @Transactional
    fun handleTranslationSuccess(payload: TranslationResultPayload) {
        logger.info("Processing translation success for videoId: {}", payload.videoId)
        val video = videoRepository.findBySourceId(payload.videoId)
            .orElseThrow { RuntimeException("Video not found: ${payload.videoId}") }

        val tempTranslation = TranslationData(
            languageCode = payload.targetLang,
            status = "TEXT_READY",
            translatedText = payload.translatedText ?: "",
            subtitles = emptyList()
        )

        val newTranslations = video.translations.filter { it.languageCode != payload.targetLang } + tempTranslation

        val videoTextReady = video.copy(
            status = VideoStatus.TRANSLATED_TEXT,
            translations = newTranslations
        )
        videoRepository.save(videoTextReady)
        logger.info("Translated text saved. Status: TRANSLATED_TEXT")

        val originalTranscription = video.sourceData!!.transcription.map {
            SubtitleItemPayload(it.text, it.start, it.end, it.conf)
        }

        if (originalTranscription.isEmpty()) {
            logger.error(
                "CRITICAL: Original transcription missing for video {}. Cannot proceed to Sync.",
                video.sourceId
            )
            return
        }

        val syncCommand = SyncCommandPayload(
            videoId = payload.videoId,
            originalText = video.sourceData.fullText,
            translatedText = payload.translatedText ?: "",
            targetLang = payload.targetLang,
            transcription = originalTranscription
        )

        val syncingVideo = videoTextReady.copy(status = VideoStatus.SYNCING)
        videoRepository.save(syncingVideo)

        logger.info("Dispatching to Sync Queue. Status: SYNCING")
        rabbitTemplate.convertAndSend(RabbitConfig.VIDEO_SYNC_CMD, syncCommand)
    }

    @Transactional
    fun handleSyncSuccess(payload: SyncResultPayload) {
        logger.info("Processing sync success for videoId: {}", payload.videoId)
        val video = videoRepository.findBySourceId(payload.videoId)
            .orElseThrow { RuntimeException("Video not found: ${payload.videoId}") }

        val existingTranslation = video.translations.find { it.languageCode == payload.targetLang }

        val finalTranslation = existingTranslation?.copy(
            status = "COMPLETED",
            subtitles = payload.subtitles.map { SubtitleItem(it.text, it.start, it.end, it.conf) }
        ) ?: TranslationData(
            languageCode = payload.targetLang,
            status = "COMPLETED",
            translatedText = "",
            subtitles = payload.subtitles.map { SubtitleItem(it.text, it.start, it.end, it.conf) }
        )

        val newTranslationsList = video.translations.filter { it.languageCode != payload.targetLang } + finalTranslation

        val finalVideo = video.copy(
            status = VideoStatus.PUBLISHED,
            translations = newTranslationsList
        )

        videoRepository.save(finalVideo)
        logger.info("CYCLE COMPLETE! Video published successfully. ID: {}", video.sourceId)
    }

    @Transactional
    fun resyncVideo(videoId: String, lang: String) {
        logger.info("Requesting Re-Sync for videoId: {} | Lang: {}", videoId, lang)

        val video = videoRepository.findBySourceId(videoId)
            .orElseThrow { RuntimeException("Video not found: $videoId") }

        if (video.sourceData == null) {
            throw RuntimeException("Cannot resync: Original transcription (SourceData) is missing.")
        }

        val existingTranslation = video.translations.find { it.languageCode == lang }
            ?: throw RuntimeException("Cannot resync: Translation text for '$lang' not found. Run translation first.")

        if (existingTranslation.translatedText.isBlank()) {
            throw RuntimeException("Cannot resync: Translated text is empty.")
        }


        val originalTranscriptionPayload = video.sourceData.transcription.map {
            SubtitleItemPayload(it.text, it.start, it.end, it.conf)
        }

        val syncCommand = SyncCommandPayload(
            videoId = video.sourceId,
            originalText = video.sourceData.fullText,
            translatedText = existingTranslation.translatedText,
            targetLang = lang,
            transcription = originalTranscriptionPayload
        )

        val syncingVideo = video.copy(status = VideoStatus.SYNCING)
        videoRepository.save(syncingVideo)

        logger.info("Dispatching existing data to Sync Queue. Status: SYNCING")
        rabbitTemplate.convertAndSend(RabbitConfig.VIDEO_SYNC_CMD, syncCommand)
    }
}