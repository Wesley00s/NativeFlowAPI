package com.content.domain.model

data class SubtitleItem(
    val text: String,
    val start: Double,
    val end: Double,
    val conf: Double? = null
)