package com.identity.domain.mapper

import com.identity.api.dto.request.SignUpRequest
import com.identity.api.dto.response.SignInResponse
import com.identity.api.dto.response.UserResponse
import com.identity.domain.model.Email
import com.identity.domain.model.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface UserMapper {
    fun map(email: Email): String = email.value

    @Mapping(target = "isActive", source = "active")
    fun toResponse(user: User): UserResponse

    fun toSignInResponse(user: User): SignInResponse

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "avatar", ignore = true)
    fun toEntity(dto: SignUpRequest): User
}