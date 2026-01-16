package com.identity.domain.mapper

import com.identity.api.dto.request.SignUpRequest
import com.identity.api.dto.response.SignUpResponse
import com.identity.domain.model.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface SignUpMapper {
    @Mapping(target = "isActive", source = "active")
    fun toDto(user: User): SignUpResponse

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "avatar", ignore = true)
    fun toEntity(dto: SignUpRequest): User
}