package com.degree.backend.repository

import com.degree.backend.model.entity.Photo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PhotoRepository : JpaRepository<Photo, Long> {
    fun findByPlaceIdOrderByOrderIndexAsc(placeId: Int): List<Photo>
}