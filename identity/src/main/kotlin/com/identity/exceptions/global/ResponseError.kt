package com.identity.exceptions.global

data class ResponseError(
    val message: String,
    val details: String? = null
)