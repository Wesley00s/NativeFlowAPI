package com.identity.api.dto.response

import com.identity.domain.enums.UserRole
import java.util.UUID

data class AuthResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: UserRole,
    val token: () -> String?
)
