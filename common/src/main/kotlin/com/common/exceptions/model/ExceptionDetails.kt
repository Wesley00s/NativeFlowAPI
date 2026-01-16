package com.common.exceptions.model

import java.time.Instant

data class ExceptionDetails(
    val title: String? = null,
    val timestamp: Instant? = null,
    val status: Int? = null,
    val exception: String? = null,
    val details: MutableMap<String?, String?> = HashMap()
)