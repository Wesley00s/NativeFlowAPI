package com.identity.api.v1.controller

import com.identity.api.dto.request.SignInRequest
import com.identity.api.dto.request.SignUpRequest
import com.identity.api.dto.response.SignInResponse
import com.identity.api.dto.response.UserResponse
import com.identity.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signUp")
    fun signUp(@RequestBody @Valid request: SignUpRequest): ResponseEntity<UserResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(request))

    @PostMapping("/signIn")
    fun signIn(
        @RequestBody @Valid request: SignInRequest,
        httpResponse: HttpServletResponse
    ): ResponseEntity<SignInResponse> =
        ResponseEntity.status(HttpStatus.OK).body(authService.signIn(request, httpResponse))

    @PostMapping("/signOut")
    fun signOut(response: HttpServletResponse): ResponseEntity<Void> {
        authService.signOut(response)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/me")
    fun getMe(auth: Authentication): ResponseEntity<UserResponse> =
        ResponseEntity.ok(authService.getMe(auth))

}