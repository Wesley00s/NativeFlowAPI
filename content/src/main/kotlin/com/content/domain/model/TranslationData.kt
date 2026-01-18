package com.content.domain.model


data class TranslationData(
    val languageCode: String,
    val status: String,
    val translatedText: String,
    val glossary: List<GlossaryTerm> = emptyList()
)

data class GlossaryTerm(
    val term: String,
    val definition: String
)