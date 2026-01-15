package com.identity.api.dto.request

import com.identity.domain.enums.UserRole
import com.identity.domain.model.Email
import com.identity.domain.model.Password

data class SignUpRequest(
    val firstName: String,
    val lastName: String,
    val email: Email,
    val password: Password,
    val role: UserRole
)