package com.content.repository

import com.content.domain.enums.VideoStatus
import com.content.domain.model.Video
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface VideoRepository: MongoRepository<Video, String> {
    fun findBySourceId(sourceId: String): Optional<Video>
    fun findAllByStatus(status: VideoStatus, pageable: Pageable): Page<Video>
    fun findAllByStatusAndTitleContainingIgnoreCase(status: VideoStatus, title: String, pageable: Pageable): Page<Video>
    fun deleteBySourceId(sourceId: String)
}