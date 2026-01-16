package com.identity.service

import com.identity.api.dto.request.SignInRequest
import com.identity.api.dto.request.SignUpRequest
import com.identity.api.dto.response.SignInResponse
import com.identity.api.dto.response.SignUpResponse
import com.identity.config.security.TokenService
import com.identity.domain.mapper.SignInMapper
import com.identity.domain.mapper.SignUpMapper
import com.identity.domain.model.Password
import com.identity.domain.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import kotlin.jvm.optionals.getOrNull

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val signUpMapper: SignUpMapper,
    private val signInMapper: SignInMapper
) {
    fun signUp(request: SignUpRequest): SignUpResponse {
        val entity = signUpMapper.toEntity(request)
        entity.password = Password(bCryptPasswordEncoder.encode(request.password.value)!!)
        val user = userRepository.save(entity)
        return signUpMapper.toDto(user)
    }

    fun signIn(request: SignInRequest, response: HttpServletResponse): SignInResponse {
        val user = userRepository.findByEmail(request.email).getOrNull()
            ?: throw BadCredentialsException("User not found")

        if (!passwordEncoder.matches(request.password.value, user.password.value)) {
            throw BadCredentialsException("Invalid password")
        }

        val token = tokenService.generateToken(user.id!!)
        addJwtCookieToResponse(token, response)
        return signInMapper.toDto(user)
    }

    private fun addJwtCookieToResponse(token: String, response: HttpServletResponse) {
        val cookie = ResponseCookie.from("nativeflow-jwt-token", token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("None")
            .maxAge(Duration.ofDays(1))
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }
}