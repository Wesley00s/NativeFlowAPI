package com.identity.exceptions.global

import com.identity.exceptions.local.InvalidTokenException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(ex: InvalidTokenException): ResponseError {
        return ResponseError(
            message = ex.message,
            details = ex.cause?.message
        )
    }
}