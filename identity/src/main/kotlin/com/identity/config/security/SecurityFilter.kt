package com.identity.config.security

import com.identity.domain.model.Email
import com.identity.domain.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Arrays

@Component
class SecurityFilter(
    private val userRepo: UserRepository,
    private val tokenService: TokenService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = recoveryToken(request)
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }
        val email = tokenService.validateToken(token)
        if (email.isNullOrEmpty()) {
            filterChain.doFilter(request, response)
            return
        }

        userRepo.findByEmail(Email(email))
            .ifPresent {
                val authorities = it.authorities
                val authentication = UsernamePasswordAuthenticationToken(
                    it,
                    null,
                    authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        filterChain.doFilter(request, response)
    }

    private fun recoveryToken(request: HttpServletRequest): String? {
        if (request.cookies != null) {
            return Arrays.stream(request.cookies)
                .filter { cookie -> cookie.name == "AUTH-TOKEN" }
                .findFirst()
                .map { cookie -> cookie.value }
                .orElse(null)
        }
        return null
    }
}