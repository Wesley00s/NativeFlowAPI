package com.identity.config.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

@Configuration
class JwtConfig {

    @Value($$"${jwt.public.key.path}")
    private lateinit var publicKeyResource: Resource

    @Value($$"${jwt.private.key.path}")
    private lateinit var privateKeyResource: Resource

    @Value($$"${jwt.issuer}")
    private lateinit var issuer: String

    @Value($$"${jwt.expiration}")
    private var expiration: Long = 0

    @Bean
    fun publicKey(): RSAPublicKey {
        val keyBytes = publicKeyResource.inputStream.readAllBytes()
        val keyString = String(keyBytes)
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")
        
        val decoded = Base64.getDecoder().decode(keyString)
        val spec = X509EncodedKeySpec(decoded)
        return KeyFactory.getInstance("RSA").generatePublic(spec) as RSAPublicKey
    }

    @Bean
    fun privateKey(): RSAPrivateKey {
        val keyBytes = privateKeyResource.inputStream.readAllBytes()
        val keyString = String(keyBytes)
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")

        val decoded = Base64.getDecoder().decode(keyString)
        val spec = PKCS8EncodedKeySpec(decoded)
        return KeyFactory.getInstance("RSA").generatePrivate(spec) as RSAPrivateKey
    }

    @Bean
    fun issuer(): String = issuer

    @Bean
    fun expiration(): Long = expiration

}