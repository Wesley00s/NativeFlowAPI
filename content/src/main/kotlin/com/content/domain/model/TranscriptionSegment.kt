package com.content.domain.model

import com.github.f4b6a3.uuid.UuidCreator

data class TranscriptionSegment(
    val id: String = UuidCreator.getTimeOrderedEpoch().toString(),
    val text: String,
    val start: Double,
    val end: Double,
    val conf: Double
)