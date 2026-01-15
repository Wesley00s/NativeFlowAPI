package com.identity.api.dto.request

import com.identity.domain.model.Email
import com.identity.domain.model.Password

data class SignInRequest(
    val email: Email,
    val password: Password
)