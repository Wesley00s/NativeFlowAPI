package com.content.api.v1.dto.request

data class AddSegmentRequest(
    val start: Double,
    val end: Double,
    val text: String
)