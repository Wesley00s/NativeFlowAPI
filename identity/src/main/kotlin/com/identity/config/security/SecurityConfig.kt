package com.identity.config.security

import com.identity.exceptions.global.CustomAccessDeniedHandler
import com.identity.exceptions.global.CustomAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val securityFilter: SecurityFilter,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v1/content/**",
                    "/v1/auth/signIn",
                    "/v1/auth/signUp",
                    "/v1/auth/signOut"
                ).permitAll()

                auth.requestMatchers("/v1/curation/**", "/v1/videos/**")
                    .hasAnyRole("ADMIN", "CREATOR")

                auth.anyRequest().authenticated()
            }

            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter::class.java)
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .exceptionHandling { handling ->
                handling.accessDeniedHandler(customAccessDeniedHandler)
                handling.authenticationEntryPoint(customAuthenticationEntryPoint)
            }.build()


    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()


    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authentication: AuthenticationConfiguration): AuthenticationManager =
        authentication.getAuthenticationManager()

}