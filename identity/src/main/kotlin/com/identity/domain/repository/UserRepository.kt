package com.identity.domain.repository

import com.identity.domain.model.Email
import com.identity.domain.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByEmail(email: Email): Optional<User>
}