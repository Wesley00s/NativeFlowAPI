package com.content.service

import com.content.api.v1.dto.CurationSegment
import com.content.api.v1.dto.PatchSegmentRequest
import com.content.api.v1.dto.request.AddSegmentRequest
import com.content.api.v1.dto.response.CurationViewResponse
import com.content.domain.enums.Lang
import com.content.domain.model.TranscriptionSegment
import com.content.domain.model.Video
import com.content.repository.VideoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class CurationService(
    private val videoRepository: VideoRepository
) {
    fun getTrack(videoId: String, lang: Lang): CurationViewResponse {
        val video = findVideoOrThrow(videoId)
        val isSource = isSourceTrack(video, lang)

        var segments: List<CurationSegment> = emptyList()
        var fullText: String?

        if (isSource) {
            segments = video.sourceData?.transcription?.map {
                CurationSegment(it.id, it.start, it.end, it.text)
            } ?: emptyList()
            fullText = video.sourceData?.fullText
        } else {
            val translation = video.translations.find {
                it.languageCode.equals(lang.llmLabel, ignoreCase = true)
            }
            fullText = translation?.translatedText
        }

        return CurationViewResponse(
            videoId = video.sourceId,
            title = video.title,
            previewUrl = video.thumbnailUrl,
            trackLanguage = lang.whisperCode,
            isSourceTrack = isSource,
            fullText = fullText,
            segments = segments
        )
    }

    @Transactional

    fun updateTranslationText(videoId: String, lang: Lang, newText: String) {
        val video = findVideoOrThrow(videoId)

        if (isSourceTrack(video, lang)) {
            throw IllegalArgumentException("To edit the original language, use the segment endpoints.")
        }

        val updatedTranslations = video.translations.map { t ->

            if (t.languageCode.equals(lang.llmLabel, ignoreCase = true)) {
                t.copy(
                    translatedText = newText,
                    status = "CURATED_TEXT",
                )
            } else {
                t
            }
        }

        val updatedVideo = video.copy(
            translations = updatedTranslations,
            updatedAt = Instant.now()
        )
        videoRepository.save(updatedVideo)
    }

    @Transactional
    fun patchOriginalSegment(videoId: String, request: PatchSegmentRequest) {
        val video = findVideoOrThrow(videoId)
        val sourceData = video.sourceData ?: throw RuntimeException("Video without original transcription")

        val currentList = sourceData.transcription
        val newList = currentList.map { item ->
            if (item.id == request.segmentId) {
                item.copy(
                    text = request.text ?: item.text,
                    start = request.start ?: item.start,
                    end = request.end ?: item.end
                )
            } else {
                item
            }
        }.sortedBy { it.start }

        val newFullText = newList.joinToString(" ") { it.text }

        val updatedSource = sourceData.copy(
            transcription = newList,
            fullText = newFullText
        )

        videoRepository.save(video.copy(sourceData = updatedSource, updatedAt = Instant.now()))
    }

    @Transactional
    fun addOriginalSegment(videoId: String, request: AddSegmentRequest) {
        val video = findVideoOrThrow(videoId)
        val sourceData = video.sourceData ?: throw RuntimeException("Video without original transcription")

        val newItem = TranscriptionSegment(
            text = request.text,
            start = request.start,
            end = request.end,
            conf = 1.0
        )

        val newList = (sourceData.transcription + newItem).sortedBy { it.start }
        val newFullText = newList.joinToString(" ") { it.text }

        val updatedSource = sourceData.copy(
            transcription = newList,
            fullText = newFullText
        )

        videoRepository.save(video.copy(sourceData = updatedSource, updatedAt = Instant.now()))
    }

    @Transactional
    fun deleteOriginalSegment(videoId: String, segmentId: String) {
        val video = findVideoOrThrow(videoId)
        val sourceData = video.sourceData ?: throw RuntimeException("Video without original transcription")

        val newList = sourceData.transcription.filter { it.id != segmentId }
        val newFullText = newList.joinToString(" ") { it.text }

        val updatedSource = sourceData.copy(
            transcription = newList,
            fullText = newFullText
        )

        videoRepository.save(video.copy(sourceData = updatedSource, updatedAt = Instant.now()))
    }

    private fun findVideoOrThrow(videoId: String) =
        videoRepository.findBySourceId(videoId)
            .orElseThrow { RuntimeException("Video not found") }

    private fun isSourceTrack(video: Video, lang: Lang): Boolean {

        return video.sourceData?.language == lang
    }
}