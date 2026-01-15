package com.identity.api.dto.response

import com.identity.domain.model.Email
import java.time.Instant

data class SignUpResponse(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: Email,
    val role: String,
    val createdAt: Instant,
    val isActive: Boolean
)
