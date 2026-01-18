package com.content.service

import com.content.api.dto.VideoTaskRequest
import com.content.api.dto.messaging.GlossaryResultPayload
import com.content.api.dto.messaging.TranscriptionResultPayload
import com.content.api.dto.messaging.TranslationCommandPayload
import com.content.api.dto.messaging.TranslationResultPayload
import com.content.api.dto.response.GlossaryResponse
import com.content.api.dto.response.TranscriptionSegmentResponse
import com.content.api.dto.response.TranslationResponse
import com.content.api.dto.response.VideoDetailsResponse
import com.content.api.dto.response.VideoListItemResponse
import com.content.domain.enums.Lang
import com.content.domain.enums.VideoStatus
import com.content.domain.model.GlossaryTerm
import com.content.domain.model.SourceData
import com.content.domain.model.TranscriptionSegment
import com.content.domain.model.TranslationData
import com.content.domain.model.Video
import com.content.messaging.config.RabbitConfig
import com.content.repository.VideoRepository
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
                logger.info("Video ${request.videoId} already transcribed. Skipping to Translation.")

                val command = TranslationCommandPayload(
                    videoId = request.videoId,
                    originalText = video.sourceData.fullText,
                    sourceLang = video.sourceData.language,
                    targetLang = request.translationLanguage.llmLabel,
                    transcription = emptyList()
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
            logger.warn("Creating new video entry for ID: {}", payload.videoId)
            Video(
                sourceId = payload.videoId,
                status = VideoStatus.PENDING,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                translations = emptyList()
            )
        }

        val targetLangLabel = payload.targetLang

        val command = TranslationCommandPayload(
            videoId = payload.videoId,
            originalText = payload.sourceData.fullText,
            sourceLang = payload.sourceData.language.llmLabel,
            targetLang = targetLangLabel,
            transcription = emptyList()
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
                language = payload.sourceData.language.llmLabel,
                fullText = payload.sourceData.fullText,
                transcription = payload.sourceData.transcription.map {
                    TranscriptionSegment(
                        text = it.text,
                        start = it.start,
                        end = it.end,
                        conf = it.conf
                    )
                }
            )
        )

        videoRepository.save(finalVideoState)
        logger.info("Video saved. Status: TRANSLATING. Dispatching to Translation Queue.")
        rabbitTemplate.convertAndSend(RabbitConfig.VIDEO_TRANSLATION_CMD, command)
    }

    @Transactional
    fun handleTranslationSuccess(payload: TranslationResultPayload) {
        logger.info("Processing translation success for videoId: {}", payload.videoId)
        val video = videoRepository.findBySourceId(payload.videoId)
            .orElseThrow { RuntimeException("Video not found: ${payload.videoId}") }

        val finalTranslation = TranslationData(
            languageCode = payload.targetLang,
            status = "COMPLETED",
            translatedText = payload.translatedText ?: ""
        )

        val newTranslations = video.translations.filter { it.languageCode != payload.targetLang } + finalTranslation

        val videoPublished = video.copy(
            status = VideoStatus.PUBLISHED,
            translations = newTranslations,
            updatedAt = Instant.now()
        )

        videoRepository.save(videoPublished)
        logger.info("CYCLE COMPLETE! Video published successfully with translated text. ID: {}", video.sourceId)
    }

    fun getPublishedVideos(title: String?, page: Int, size: Int): Page<VideoListItemResponse> {
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val videoPage = if (title.isNullOrBlank()) {
            videoRepository.findAllByStatus(VideoStatus.PUBLISHED, pageable)
        } else {
            videoRepository.findAllByStatusAndTitleContainingIgnoreCase(VideoStatus.PUBLISHED, title, pageable)
        }

        return videoPage.map { video ->
            VideoListItemResponse(
                id = video.sourceId,
                title = video.title ?: "UNKNOW",
                description = video.author?.let { "Canal: $it" },
                thumbnailUrl = video.thumbnailUrl,
                durationSeconds = video.durationSeconds,
                lang = Lang.findMatch(video.sourceData?.language) ?: Lang.EN_US,
                status = video.status
            )
        }
    }

    fun getVideoDetails(videoId: String, requestedLang: Lang?): VideoDetailsResponse {
        val video = videoRepository.findBySourceId(videoId)
            .orElseThrow { RuntimeException("Video not found with ID: $videoId") }

        val sourceData = video.sourceData
            ?: throw RuntimeException("Video data is incomplete for ID: $videoId")

        val availableLangs = video.translations.mapNotNull { translation ->
            Lang.findMatch(translation.languageCode, translation.languageCode)
        }

        val translationData = if (requestedLang != null) {
            video.translations.find {
                Lang.findMatch(it.languageCode, it.languageCode) == requestedLang
            }
        } else {
            null
        }

        return VideoDetailsResponse(
            id = video.sourceId,
            title = video.title ?: "Unknown Title",
            author = video.author,
            videoUrl = "https://www.youtube.com/watch?v=${video.sourceId}",
            durationSeconds = video.durationSeconds,
            status = video.status,
            originalLanguage = Lang.findMatch(sourceData.language) ?: Lang.EN_US,

            availableLanguages = availableLangs,

            transcription = sourceData.transcription.map {
                TranscriptionSegmentResponse(
                    text = it.text,
                    start = it.start,
                    end = it.end
                )
            },
            translation = translationData?.let {
                TranslationResponse(
                    language = requestedLang ?: Lang.PT_BR,
                    fullText = it.translatedText,
                    glossary = it.glossary.map { term ->
                        GlossaryResponse(term.term, term.definition)
                    }
                )
            }
        )
    }

    @Transactional
    fun handleGlossarySuccess(payload: GlossaryResultPayload) {
        logger.info("Processing glossary for videoId: {}", payload.videoId)

        val video = videoRepository.findBySourceId(payload.videoId)
            .orElseThrow { RuntimeException("Video not found: ${payload.videoId}") }

        val targetLang = payload.targetLang?.whisperCode ?: video.translations.lastOrNull()?.languageCode

        if (targetLang == null) {
            logger.warn("Could not determine language for glossary. Skipping.")
            return
        }
        
        val newGlossaryTerms = payload.items.map {
            GlossaryTerm(term = it.term, definition = it.definition)
        }
        
        val updatedTranslations = video.translations.map { translation ->
            if (translation.languageCode == targetLang) {
                translation.copy(glossary = newGlossaryTerms)
            } else {
                translation
            }
        }

        val videoUpdated = video.copy(
            translations = updatedTranslations,
            updatedAt = Instant.now()
        )

        videoRepository.save(videoUpdated)
        logger.info("Glossary saved for video {} (Lang: {}). Items: {}", video.sourceId, targetLang, newGlossaryTerms.size)
    }
}