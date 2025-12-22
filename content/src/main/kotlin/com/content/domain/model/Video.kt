package com.content.domain.model

import com.content.domain.enums.VideoStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "videos")
data class Video(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val sourceId: String,
    val title: String? = null,
    val author: String? = null,
    val viewCount: Long = 0,
    val thumbnailUrl: String? = null,
    val durationSeconds: Long = 0,
    val status: VideoStatus = VideoStatus.PENDING,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val sourceData: SourceData? = null,
    val translations: List<TranslationData> = emptyList(),
)

data class SourceData(
    val language: String,
    val fullText: String,
    val transcription: List<TranscriptionSegment>
)

data class TranslationData(
    val languageCode: String,
    val status: String,
    val translatedText: String,
    val subtitles: List<SubtitleItem> = emptyList()
)

data class TranscriptionSegment(
    val text: String,
    val start: Double,
    val end: Double,
    val conf: Double
)

data class SubtitleItem(
    val text: String,
    val start: Double,
    val end: Double,
    val conf: Double? = null
)