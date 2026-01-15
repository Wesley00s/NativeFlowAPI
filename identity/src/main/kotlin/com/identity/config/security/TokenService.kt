package com.identity.config.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.identity.exceptions.local.InvalidTokenException
import org.springframework.stereotype.Service
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.time.Instant
import java.util.Date
import java.util.UUID

@Service
class TokenService(
    val publicKey: RSAPublicKey,
    val privateKey: RSAPrivateKey,
    val issuer: String,
    val expiration: Long
) {
    fun generateToken(userId: UUID): String {
        val algorithm = Algorithm.RSA256(publicKey, privateKey)
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(userId.toString())
            .withIssuedAt(Instant.now())
            .withExpiresAt(Date.from(Instant.now().plusMillis(expiration)))
            .sign(algorithm)

    }


    fun validateToken(token: String): String? = try {
        val algorithm = Algorithm.RSA256(publicKey, privateKey)
        JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
            .verify(token)
            .subject
    } catch (e: Exception) {
        throw InvalidTokenException(e)
    }
}