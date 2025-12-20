package com.degree.backend.repository

import com.degree.backend.model.entity.Favorite
import com.degree.backend.model.entity.FavoriteId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FavoriteRepository : JpaRepository<Favorite, FavoriteId> {

    // Список избранного для конкретного пользователя (экран "Избранное")
    fun findByUserId(userId: Long): List<Favorite>

    // Проверить, лайкнул ли пользователь это место (чтобы закрасить сердечко на UI)
    fun existsByUserIdAndPlaceId(userId: Long, placeId: Int): Boolean

    // Удалить лайк/избранное (нужно указывать оба ID)
    fun deleteByUserIdAndPlaceId(userId: Long, placeId: Int)
}