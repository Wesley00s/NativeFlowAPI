package com.content.api.v1.controller

import com.content.api.dto.response.ApiResponse
import com.content.api.dto.response.PaginationResponse
import com.content.api.dto.response.VideoDetailsResponse
import com.content.api.dto.response.VideoListItemResponse
import com.content.domain.enums.Lang
import com.content.service.VideoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/content")
class ContentFeedController(
    private val videoService: VideoService
) {

    @GetMapping
    fun getFeed(
        @RequestParam(name = "title", required = false) title: String?,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<VideoListItemResponse>> {
        val page = videoService.getPublishedVideos(
            title = title,
            page = page,
            size = size
        )
        return ResponseEntity.ok(
            ApiResponse(
                data = page.content,
                pagination = PaginationResponse(
                    page = page.pageable.pageNumber,
                    size = page.pageable.pageSize,
                    totalElements = page.totalElements,
                    totalPages = page.totalPages
                )
            )

        )
    }

    @GetMapping("/{videoId}")
    fun getVideoDetails(
        @PathVariable videoId: String,
        @RequestParam(name = "lang", required = false) lang: Lang?
    ): ResponseEntity<VideoDetailsResponse> =
        ResponseEntity.ok(
            videoService
                .getVideoDetails(
                    videoId,
                    lang
                )
        )

}