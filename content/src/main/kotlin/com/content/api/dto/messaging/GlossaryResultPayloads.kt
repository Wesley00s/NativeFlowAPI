package com.content.api.dto.messaging

import com.content.domain.enums.Lang
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GlossaryResultPayload(
    val videoId: String,
    val targetLang: Lang?,
    val items: List<GlossaryItemDto> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GlossaryItemDto(
    val term: String,
    val definition: String
)