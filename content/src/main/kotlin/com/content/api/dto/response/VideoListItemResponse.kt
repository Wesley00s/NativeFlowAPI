package com.content.api.dto.response

import com.content.domain.enums.Lang
import com.content.domain.enums.VideoStatus

data class VideoListItemResponse(
    val id: String,
    val title: String,
    val description: String?,
    val thumbnailUrl: String?,
    val durationSeconds: Long,
    val lang: Lang,
    val status: VideoStatus
)