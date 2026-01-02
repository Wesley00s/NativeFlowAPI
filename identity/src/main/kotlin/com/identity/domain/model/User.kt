package com.identity.domain.model

import com.identity.domain.enums.UserRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant
import java.util.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID,
    val avatar: String?,
    @Column(nullable = false) val firstName: String,
    @Column(nullable = false) val lastName: String,
    @Column(unique = true, nullable = false) val email: Email,
    @Column(nullable = false) val password: Password,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false) val role: UserRole,
    @Column(nullable = false) var createdAt: Instant,
    @Column(nullable = false) val isActive: Boolean = true

) : UserDetails {

    @PrePersist
    fun onCreate() {
        createdAt = Instant.now()
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(
            GrantedAuthority { "ROLE_${role.name}" }
        )
    }

    override fun getPassword(): String? {
        return password.password
    }

    override fun getUsername(): String {
        return email.emailAddress
    }
}