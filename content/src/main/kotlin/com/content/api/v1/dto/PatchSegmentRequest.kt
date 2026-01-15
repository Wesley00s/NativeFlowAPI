package com.content.api.v1.dto


data class PatchSegmentRequest(
    val segmentId: String,
    val text: String? = null,
    val start: Double? = null,
    val end: Double? = null
)