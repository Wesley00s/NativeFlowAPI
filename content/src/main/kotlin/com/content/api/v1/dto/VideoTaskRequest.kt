package com.content.api.v1.dto

data class VideoTaskRequest(
    val videoId: String,
    val language: String,
    val translationLanguage: String,
    val tags: List<String>? = emptyList()
)