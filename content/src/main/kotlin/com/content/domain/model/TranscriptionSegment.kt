package com.content.domain.model

data class TranscriptionSegment(
    val text: String,
    val start: Double,
    val end: Double,
    val conf: Double
)