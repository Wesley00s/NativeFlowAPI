package com.content.api.v1.dto.response

import com.content.domain.enums.Lang
import com.content.domain.enums.VideoStatus

data class VideoDetailsResponse(
    val id: String,
    val title: String,
    val author: String?,
    val videoUrl: String,
    val durationSeconds: Long,
    val level: String,
    val status: VideoStatus,
    val originalLanguage: Lang,
    val availableLanguages: List<Lang>,
    val transcription: List<TranscriptionSegmentResponse>,
    val translation: TranslationResponse?
)

data class TranscriptionSegmentResponse(
    val text: String,
    val start: Double,
    val end: Double
)

data class TranslationResponse(
    val language: Lang,
    val fullText: String
)