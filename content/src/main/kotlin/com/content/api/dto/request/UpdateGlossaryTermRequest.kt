package com.content.api.dto.request

data class UpdateGlossaryTermRequest(
    val term: String,
    val definition: String
)