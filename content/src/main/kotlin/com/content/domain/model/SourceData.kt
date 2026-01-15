package com.content.domain.model

import com.content.domain.enums.Lang

data class SourceData(
    val language: Lang,
    val fullText: String,
    val transcription: List<TranscriptionSegment>
)