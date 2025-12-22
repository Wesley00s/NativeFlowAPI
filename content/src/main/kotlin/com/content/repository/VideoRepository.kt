package com.content.repository

import com.content.domain.model.Video
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface VideoRepository: MongoRepository<Video, String> {
    fun findBySourceId(sourceId: String): Optional<Video>
}