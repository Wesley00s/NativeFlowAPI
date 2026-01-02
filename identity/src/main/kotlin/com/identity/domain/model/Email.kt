package com.identity.domain.model

import jakarta.persistence.Embeddable

@Embeddable
class Email(
    val emailAddress: String
) {
    init {
        require(isValidEmail(emailAddress)) { "Invalid email address: $emailAddress" }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"""
        return Regex(emailRegex).matches(email)
    }
}