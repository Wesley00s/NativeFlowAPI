package com.content.service

import com.content.api.dto.request.UpdateGlossaryTermRequest
import com.content.api.dto.response.GlossaryResponse
import com.content.domain.enums.Lang
import com.content.domain.model.GlossaryTerm
import com.content.repository.VideoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GlossaryService(
    private val videoRepository: VideoRepository
) {

    @Transactional
    fun updateGlossaryTerm(
        videoId: String,
        lang: Lang,
        originalTerm: String,
        request: UpdateGlossaryTermRequest
    ): List<GlossaryResponse> {
        val video = videoRepository.findBySourceId(videoId)
            .orElseThrow { RuntimeException("Video not found: $videoId") }

        val translationIndex = video.translations.indexOfFirst {
            Lang.findMatch(it.languageCode, it.languageCode) == lang
        }

        if (translationIndex == -1) {
            throw RuntimeException("Translation not found for language: $lang")
        }

        val translation = video.translations[translationIndex]

        val termExists = translation.glossary.any { it.term.equals(originalTerm, ignoreCase = true) }
        if (!termExists) {
            throw RuntimeException("Glossary term '$originalTerm' not found")
        }

        val newGlossaryList = translation.glossary.map { item ->
            if (item.term.equals(originalTerm, ignoreCase = true)) {
                GlossaryTerm(term = request.term, definition = request.definition)
            } else {
                item
            }
        }

        val newTranslationData = translation.copy(glossary = newGlossaryList)
        val newTranslationsList = video.translations.toMutableList()
        newTranslationsList[translationIndex] = newTranslationData

        videoRepository.save(video.copy(translations = newTranslationsList))

        return newGlossaryList.map { GlossaryResponse(it.term, it.definition) }
    }

    @Transactional
    fun deleteGlossaryTerm(
        videoId: String,
        lang: Lang,
        termToDelete: String
    ): List<GlossaryResponse> {
        val video = videoRepository.findBySourceId(videoId)
            .orElseThrow { RuntimeException("Video not found: $videoId") }

        val translationIndex = video.translations.indexOfFirst {
            Lang.findMatch(it.languageCode, it.languageCode) == lang
        }

        if (translationIndex == -1) {
            throw RuntimeException("Translation not found for language: $lang")
        }

        val translation = video.translations[translationIndex]

        val newGlossaryList = translation.glossary.filterNot {
            it.term.equals(termToDelete, ignoreCase = true)
        }

        if (newGlossaryList.size == translation.glossary.size) {
            throw RuntimeException("Glossary term '$termToDelete' not found")
        }

        val newTranslationData = translation.copy(glossary = newGlossaryList)
        val newTranslationsList = video.translations.toMutableList()
        newTranslationsList[translationIndex] = newTranslationData

        videoRepository.save(video.copy(translations = newTranslationsList))

        return newGlossaryList.map { GlossaryResponse(it.term, it.definition) }
    }
}