package com.content.api.dto.response

import com.content.api.dto.CurationSegment

data class CurationViewResponse(
    val videoId: String,
    val title: String?,
    val previewUrl: String?,
    val trackLanguage: String,
    val isSourceTrack: Boolean,
    val fullText: String?,
    val segments: List<CurationSegment>
)