package com.identity.api.v1.controller

import com.identity.api.dto.request.SignInRequest
import com.identity.api.dto.request.SignUpRequest
import com.identity.api.dto.response.SignInResponse
import com.identity.api.dto.response.SignUpResponse
import com.identity.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid request: SignUpRequest): ResponseEntity<SignUpResponse> {
        val response = authService.signUp(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody @Valid request: SignInRequest, httpResponse: HttpServletResponse): ResponseEntity<SignInResponse> {
        val response = authService.signIn(request, httpResponse)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

}