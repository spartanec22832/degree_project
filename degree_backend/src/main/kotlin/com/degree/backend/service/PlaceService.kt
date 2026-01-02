package com.degree.backend.service

import com.degree.backend.model.dto.PlaceDto
import com.degree.backend.model.dto.PlaceMapDto
import com.degree.backend.model.mapper.toDto
import com.degree.backend.model.mapper.toMapDto
import com.degree.backend.repository.FavoriteRepository
import com.degree.backend.repository.PhotoRepository
import com.degree.backend.repository.PlaceRepository
import com.degree.backend.repository.RatingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PlaceService(
    private val placeRepository: PlaceRepository,
    private val ratingRepository: RatingRepository,
    private val favoriteRepository: FavoriteRepository,
    private val photoRepository: PhotoRepository
) {

    // 1. Метод для карты (максимально быстрый и легкий)
    fun getAllPlacesForMap(): List<PlaceMapDto> {
        return placeRepository.findAll()
            .map { it.toMapDto() }
    }

    // 2. Метод для детального экрана места
    // userId может быть null, если смотрит неавторизованный гость (на будущее)
    fun getPlaceDetails(placeId: Int, userId: Long?): PlaceDto {
        // 1. Ищем само место
        val place = placeRepository.findById(placeId)
            .orElseThrow { RuntimeException("Place not found with id: $placeId") }

        // 2. Загружаем фото
        val photos = photoRepository.findByPlaceIdOrderByOrderIndexAsc(placeId)

        // 3. Считаем средний рейтинг
        val averageRating = ratingRepository.getAverageRatingByPlaceId(placeId)

        // 4. Проверяем, лайкнул ли его текущий пользователь
        val isFavorite = if (userId != null) {
            favoriteRepository.existsByUserIdAndPlaceId(userId, placeId)
        } else {
            false
        }

        // 5. Собираем всё вместе
        return place.toDto(
            isFavorite = isFavorite,
            averageRating = averageRating,
            placePhotos = photos
        )
    }

    // Дополнительно: Поиск (понадобится для SearchBar)
    fun searchPlaces(query: String): List<PlaceMapDto> {
        return placeRepository.findByNameContainingIgnoreCase(query)
            .map { it.toMapDto() }
    }
}