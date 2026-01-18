package com.content.domain.model


data class SourceData(
    val language: String,
    val fullText: String,
    val transcription: List<TranscriptionSegment>
)