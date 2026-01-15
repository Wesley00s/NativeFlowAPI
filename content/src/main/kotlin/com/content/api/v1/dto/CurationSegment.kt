package com.content.api.v1.dto

data class CurationSegment(
    val id: String,
    val start: Double,
    val end: Double,
    val text: String
)