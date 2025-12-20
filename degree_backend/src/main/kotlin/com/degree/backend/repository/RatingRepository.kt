package com.degree.backend.repository

import com.degree.backend.model.entity.Rating
import com.degree.backend.model.entity.RatingId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RatingRepository : JpaRepository<Rating, RatingId> {

    // Все оценки пользователя
    fun findByUserId(userId: Long): List<Rating>

    // Все оценки места
    fun findByPlaceId(placeId: Int): List<Rating>
}