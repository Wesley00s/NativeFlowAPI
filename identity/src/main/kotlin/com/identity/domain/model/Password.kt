package com.identity.domain.model

import jakarta.persistence.Embeddable

@Embeddable
class Password(
    val password: String
) {
    init {
        require(isValidPassword(password)) { "Invalid password format." }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordRegex = """^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$"""
        return Regex(passwordRegex).matches(password)
    }
}