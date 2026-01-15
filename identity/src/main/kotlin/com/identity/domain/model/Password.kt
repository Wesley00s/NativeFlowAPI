package com.identity.domain.model

import jakarta.persistence.Embeddable

@Embeddable
class Password(
    val value: String
) {
    init {
        require(isValidPassword(value)) {
            "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$"""
        return Regex(passwordRegex).matches(password)
    }
}