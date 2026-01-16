package com.nativeflow.exceptions.global

import com.common.exceptions.model.ExceptionDetails
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.time.Instant

@Component
object ExceptionsUtils {
    fun createErrorResponse(
        ex: Exception?,
        title: String?,
        status: HttpStatus
    ): ResponseEntity<ExceptionDetails> {
        val errors: MutableMap<String?, String?> = HashMap()
        errors["cause"] = ex?.message

        return ResponseEntity.status(status).body(
            ExceptionDetails(title, Instant.now(), status.value(), ex?.javaClass?.getName(), errors)
        )
    }
}