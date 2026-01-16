package com.identity.service

import com.common.constansts.SecurityConstants
import com.identity.api.dto.request.SignInRequest
import com.identity.api.dto.request.SignUpRequest
import com.identity.api.dto.response.SignInResponse
import com.identity.api.dto.response.UserResponse
import com.identity.config.security.TokenService
import com.identity.domain.mapper.UserMapper
import com.identity.domain.model.Password
import com.identity.domain.model.User
import com.identity.domain.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
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
    private val userMapper: UserMapper
) {
    fun signUp(request: SignUpRequest): UserResponse {
        val entity = userMapper.toEntity(request)
        entity.password = Password(bCryptPasswordEncoder.encode(request.password.value)!!)
        val user = userRepository.save(entity)
        return userMapper.toResponse(user)
    }

    fun signIn(request: SignInRequest, response: HttpServletResponse): SignInResponse {
        val user = userRepository.findByEmail(request.email).getOrNull()
            ?: throw BadCredentialsException("User not found")

        if (!passwordEncoder.matches(request.password.value, user.password.value)) {
            throw BadCredentialsException("Invalid password")
        }

        val token = tokenService.generateToken(user.id!!)
        addJwtCookieToResponse(token, response)
        return userMapper.toSignInResponse(user)
    }

    fun signOut(response: HttpServletResponse) {
        response.addHeader(
            HttpHeaders.SET_COOKIE, ResponseCookie.from(SecurityConstants.AUTH_COOKIE_NAME, "")
                .maxAge(Duration.ZERO)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .build()
                .toString()
        )
    }

    fun getMe(auth: Authentication): UserResponse =
        userMapper.toResponse(auth.principal as User)

    private fun addJwtCookieToResponse(token: String, response: HttpServletResponse) {
        val cookie = ResponseCookie.from(SecurityConstants.AUTH_COOKIE_NAME, token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("None")
            .maxAge(Duration.ofDays(1))
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }
}