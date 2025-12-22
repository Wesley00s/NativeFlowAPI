package com.content.api.v1.controller

import com.content.api.v1.dto.response.TextResponse
import com.content.service.VideoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/videos")
class VideoController(
    private val videoService: VideoService
) {
    @PostMapping("/{videoId}/resync")
    fun resyncVideo(
        @PathVariable videoId: String,
        @RequestParam(defaultValue = "ptbr") lang: String
    ): ResponseEntity<TextResponse> {
        videoService.resyncVideo(videoId, lang)
        return ResponseEntity.accepted().body(
            TextResponse(
                text = "Resync initiated for video $videoId in language $lang",
                statusCode = 202
            )
        )
    }
}