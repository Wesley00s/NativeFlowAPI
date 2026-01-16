package com.identity.api.dto.response

import com.identity.domain.enums.UserRole
import com.identity.domain.model.Email
import java.time.Instant
import java.util.UUID

data class UserResponse(

    val id: UUID,
    val firstName: String,
    val lastName: String,
    val avatar: String? = null,
    val email: Email,
    val role: UserRole,
    val createdAt: Instant,
    val isActive: Boolean
)
