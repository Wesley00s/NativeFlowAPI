package com.content.api.v1.controller

import com.content.api.v1.dto.PatchSegmentRequest
import com.content.api.v1.dto.request.AddSegmentRequest
import com.content.api.v1.dto.request.UpdateTextRequest
import com.content.api.v1.dto.response.CurationViewResponse
import com.content.domain.enums.Lang
import com.content.service.CurationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/curation")
class CurationController(
    private val curationService: CurationService
) {
    @GetMapping("/{videoId}")
    fun getTrack(
        @PathVariable videoId: String,
        @RequestParam lang: Lang 
    ): ResponseEntity<CurationViewResponse> {
        val view = curationService.getTrack(videoId, lang)
        return ResponseEntity.ok(view)
    }

    @PutMapping("/{videoId}/text")
    fun updateTranslationText(
        @PathVariable videoId: String,
        @RequestParam lang: Lang,
        @RequestBody request: UpdateTextRequest
    ): ResponseEntity<Void> {
        curationService.updateTranslationText(videoId, lang, request.text)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{videoId}/segments")
    fun patchOriginalSegment(
        @PathVariable videoId: String,
        @RequestBody request: PatchSegmentRequest
    ): ResponseEntity<Void> {
        curationService.patchOriginalSegment(videoId, request)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{videoId}/segments")
    fun addOriginalSegment(
        @PathVariable videoId: String,
        @RequestBody request: AddSegmentRequest
    ): ResponseEntity<Void> {
        curationService.addOriginalSegment(videoId, request)
        return ResponseEntity.status(201).build()
    }

    @DeleteMapping("/{videoId}/segments/{segmentId}")
    fun deleteOriginalSegment(
        @PathVariable videoId: String,
        @PathVariable segmentId: String
    ): ResponseEntity<Void> {
        curationService.deleteOriginalSegment(videoId, segmentId)
        return ResponseEntity.noContent().build()
    }
}