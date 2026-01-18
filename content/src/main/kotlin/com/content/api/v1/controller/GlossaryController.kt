package com.content.api.v1.controller

import com.content.api.dto.request.UpdateGlossaryTermRequest
import com.content.api.dto.response.GlossaryResponse
import com.content.domain.enums.Lang
import com.content.service.GlossaryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/videos")
class GlossaryController(
    private val glossaryService: GlossaryService
) {

    @PutMapping("/{videoId}/glossary/{lang}/{term}")
    fun updateTerm(
        @PathVariable videoId: String,
        @PathVariable lang: Lang,
        @PathVariable term: String,
        @RequestBody request: UpdateGlossaryTermRequest
    ): ResponseEntity<List<GlossaryResponse>> {
        val updatedGlossary = glossaryService.updateGlossaryTerm(videoId, lang, term, request)
        return ResponseEntity.ok(updatedGlossary)
    }

    @DeleteMapping("/{videoId}/glossary/{lang}/{term}")
    fun deleteTerm(
        @PathVariable videoId: String,
        @PathVariable lang: Lang,
        @PathVariable term: String
    ): ResponseEntity<List<GlossaryResponse>> {
        val updatedGlossary = glossaryService.deleteGlossaryTerm(videoId, lang, term)
        return ResponseEntity.ok(updatedGlossary)
    }
}