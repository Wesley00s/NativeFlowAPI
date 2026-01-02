package com.identity.exceptions.local

class InvalidTokenException(
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message, cause) {
    constructor(cause: Throwable?) : this(
        message = cause?.message ?: "Invalid token.",
        cause = cause
    )
}