package com.identity.config.security

import com.common.constansts.SecurityConstants
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
import java.util.UUID 

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
        try {
            val token = recoveryToken(request)

            if (token == null) {
                filterChain.doFilter(request, response)
                return
            }

            val userIdString = tokenService.validateToken(token)

            if (!userIdString.isNullOrEmpty()) {
                val userId = UUID.fromString(userIdString)
                userRepo.findById(userId).ifPresent { user ->
                    val authentication = UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.authorities
                    )
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }

            filterChain.doFilter(request, response)
        } catch (_: Exception) {
            filterChain.doFilter(request, response)
        }
    }

    private fun recoveryToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "")
        }

        if (request.cookies != null) {
            return Arrays.stream(request.cookies)
                .filter { cookie -> cookie.name == SecurityConstants.AUTH_COOKIE_NAME }
                .findFirst()
                .map { cookie -> cookie.value }
                .orElse(null)
        }
        return null
    }
}