package com.identity.domain.model

import com.identity.domain.enums.UserRole
import jakarta.persistence.AttributeOverride
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
@Table(name = "user_tb")
data class User(
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id val id: UUID? = null,
    val avatar: String? = null,
    @Column(nullable = false) val firstName: String,
    @Column(nullable = false) val lastName: String,
    @AttributeOverride(
        name = "value",
        column = Column(
            name = "email",
            unique = true,
            nullable = false
        )
    )
    val email: Email,
    @AttributeOverride(
        name = "value",
        column = Column(
            name = "password",
            nullable = false
        )
    )
    var password: Password,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false) val role: UserRole,
    @Column(nullable = false) var createdAt: Instant = Instant.now(),
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
        return password.value
    }

    override fun getUsername(): String {
        return email.value
    }
}