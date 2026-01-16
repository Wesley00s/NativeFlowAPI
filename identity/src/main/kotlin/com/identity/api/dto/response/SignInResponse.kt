package com.identity.api.dto.response

import com.identity.domain.enums.UserRole
import java.util.UUID

data class SignInResponse(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: UserRole
)
