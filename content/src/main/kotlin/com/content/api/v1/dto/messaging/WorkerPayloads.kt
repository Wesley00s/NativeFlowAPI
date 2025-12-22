package com.content.api.v1.dto.messaging

import com.fasterxml.jackson.annotation.JsonIgnoreProperties


@JsonIgnoreProperties(ignoreUnknown = true)
data class TranscriptionResultPayload(
    val videoId: String,
    val status: String,
    val duration: Double,
    val targetLang: String,
    val sourceData: SourceDataPayload,
    val errorMessage: String? = null
)

data class SourceDataPayload(
    val language: String,
    val fullText: String,
    val transcription: List<SubtitleItemPayload>,
    val title: String? = null,
    val author: String? = null,
    val viewCount: Long = 0,
    val thumbnailUrl: String? = null
)

data class TranslationCommandPayload(
    val videoId: String,
    val originalText: String,
    val sourceLang: String,
    val targetLang: String,
    val transcription: List<SubtitleItemPayload>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TranslationResultPayload(
    val videoId: String,
    val status: String,
    val targetLang: String,
    val translatedText: String? = null,
    val subtitles: List<SubtitleItemPayload>? = emptyList(),
    val errorMessage: String? = null
)

data class SubtitleItemPayload(
    val text: String,
    val start: Double,
    val end: Double,
    val conf: Double
)

data class SyncCommandPayload(
    val videoId: String,
    val originalText: String,
    val translatedText: String,
    val targetLang: String,
    val transcription: List<SubtitleItemPayload>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SyncResultPayload(
    val videoId: String,
    val status: String,
    val targetLang: String,
    val subtitles: List<SubtitleItemPayload> = emptyList(),
    val errorMessage: String? = null
)