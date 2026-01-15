package com.identity.domain.mapper

import com.identity.api.dto.response.SignInResponse
import com.identity.domain.model.Email
import com.identity.domain.model.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface SignInMapper {

    fun map(email: Email): String = email.value

    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "token", source = "token")
    fun toDto(user: User, token: String): SignInResponse
}