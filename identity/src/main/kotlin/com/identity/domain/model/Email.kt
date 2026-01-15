package com.identity.domain.model

import jakarta.persistence.Embeddable

@Embeddable
class Email(
    val value: String
) {
    init {
        require(isValidEmail(value)) { "Invalid email address: $value" }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"""
        return Regex(emailRegex).matches(email)
    }
}