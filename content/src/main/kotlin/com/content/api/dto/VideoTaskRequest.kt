package com.content.api.dto

import com.content.domain.enums.Lang

data class VideoTaskRequest(
    val videoId: String,
    val language: Lang,
    val translationLanguage: Lang,
    val tags: List<String>? = emptyList()
)