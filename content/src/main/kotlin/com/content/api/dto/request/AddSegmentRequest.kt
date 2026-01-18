package com.content.api.dto.request

data class AddSegmentRequest(
    val start: Double,
    val end: Double,
    val text: String
)