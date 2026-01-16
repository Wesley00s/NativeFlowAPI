package com.nativeflow.exceptions.global

import com.identity.exceptions.local.InvalidTokenException
import com.common.exceptions.model.ExceptionDetails
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MultipartException
import java.nio.file.AccessDeniedException
import java.time.Instant
import java.util.function.Consumer

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidException(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionDetails> {
        val errors: MutableMap<String?, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError? ->
            val fieldName = if (error is FieldError) error.field else error!!.objectName
            errors[fieldName] = error.defaultMessage
        })
        return ResponseEntity.badRequest().body(
            ExceptionDetails(
                "Bad Request: Invalid fields",
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.javaClass.getName(),
                errors
            )
        )
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(ex: InvalidTokenException): ResponseEntity<ExceptionDetails> {
        return ExceptionsUtils.createErrorResponse(ex, ex.message, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(EntityNotFoundException::class, NoSuchElementException::class)
    fun handleNotFound(ex: Exception): ResponseEntity<ExceptionDetails> {
        return ExceptionsUtils.createErrorResponse(ex, "Resource not found.", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ExceptionDetails> {
        return ExceptionsUtils.createErrorResponse(ex, "Access Denied.", HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ExceptionDetails> {
        return ExceptionsUtils.createErrorResponse(
            ex,
            "An unexpected internal error occurred.",
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(MultipartException::class)
    fun handleMultipartException(ex: MultipartException): ResponseEntity<ExceptionDetails> {
        return ExceptionsUtils.createErrorResponse(
            ex,
            "Upload Error: The request is malformed or incomplete.",
            HttpStatus.BAD_REQUEST
        )
    }
}