package com.content.api.dto.response

data class ApiResponse<T>(
    val data: List<T>,
    val pagination: PaginationResponse
)

data class PaginationResponse(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)