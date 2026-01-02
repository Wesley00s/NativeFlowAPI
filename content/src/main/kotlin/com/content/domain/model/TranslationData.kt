package com.content.domain.model

data class TranslationData(
    val languageCode: String,
    val status: String,
    val translatedText: String,
    val subtitles: List<SubtitleItem> = emptyList()
)