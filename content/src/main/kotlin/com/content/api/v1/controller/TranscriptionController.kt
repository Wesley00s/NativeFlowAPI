package com.content.api.v1.controller

import com.content.api.dto.VideoTaskRequest
import com.content.api.dto.response.TextResponse
import com.content.service.VideoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/videos")
class TranscriptionController(
    private val videoService: VideoService
) {
    @PostMapping("/publish")
    fun sendToQueue(@RequestBody request: VideoTaskRequest): ResponseEntity<TextResponse> {
        videoService.initiateProcessing(request)
        return ResponseEntity.accepted().body(
            TextResponse(
                text = "Processing started for ${request.videoId}",
                statusCode = 202
            )
        )
    }
}