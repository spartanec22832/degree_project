package com.degree.backend.service

import com.degree.backend.model.dto.FavoriteRequest
import com.degree.backend.model.dto.PlaceDto
import com.degree.backend.model.dto.RatingRequest
import com.degree.backend.model.entity.RatingId
import com.degree.backend.model.mapper.toDto
import com.degree.backend.model.mapper.toEntity
import com.degree.backend.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
@Transactional(readOnly = true)
class InteractionService(
    private val favoriteRepository: FavoriteRepository,
    private val ratingRepository: RatingRepository,
    private val placeRepository: PlaceRepository,
    private val userRepository: UserRepository,
    private val photoRepository: PhotoRepository // Нужно для сборки PlaceDto
) {

    // --- ИЗБРАННОЕ (FAVORITES) ---

    @Transactional
    fun toggleFavorite(userId: Long, request: FavoriteRequest) {
        // Проверяем наличие. Твой репозиторий имеет специальный метод для этого.
        val exists = favoriteRepository.existsByUserIdAndPlaceId(userId, request.placeId)

        if (exists) {
            // Удаляем. Метод deleteBy... требует активной транзакции (она есть благодаря @Transactional)
            favoriteRepository.deleteByUserIdAndPlaceId(userId, request.placeId)
        } else {
            // Создаем новое
            val user = userRepository.findById(userId)
                .orElseThrow { RuntimeException("User not found") }
            val place = placeRepository.findById(request.placeId)
                .orElseThrow { RuntimeException("Place not found") }

            // Используем твой маппер
            val favorite = request.toEntity(user, place)
            favoriteRepository.save(favorite)
        }
    }

    /**
     * Возвращает список избранного.
     * Здесь мы собираем полноценные PlaceDto (с фото и рейтингом),
     * чтобы экран "Избранное" выглядел красиво.
     */
    fun getUserFavorites(userId: Long): List<PlaceDto> {
        // 1. Достаем список сущностей Favorite
        val favorites = favoriteRepository.findByUserId(userId)

        // 2. Преобразуем каждую запись в PlaceDto
        return favorites.map { favorite ->
            val place = favorite.place

            // Подгружаем фото для этого места
            val photos = photoRepository.findByPlaceIdOrderByOrderIndexAsc(place.id)

            // Подгружаем средний рейтинг
            val avgRating = ratingRepository.getAverageRatingByPlaceId(place.id)

            // Маппим в DTO.
            // isFavorite = true, так как мы берем данные из таблицы избранного
            place.toDto(
                isFavorite = true,
                averageRating = avgRating,
                placePhotos = photos
            )
        }
    }

    // --- РЕЙТИНГИ (RATINGS) ---

    @Transactional
    fun setRating(userId: Long, request: RatingRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        val place = placeRepository.findById(request.placeId)
            .orElseThrow { RuntimeException("Place not found") }

        // Поиск существующей оценки.
        // Так как у тебя @EmbeddedId, мы можем искать по составному ключу RatingId
        val ratingId = RatingId(userId = userId, placeId = request.placeId)
        val existingRatingOpt = ratingRepository.findById(ratingId)

        if (existingRatingOpt.isPresent) {
            // Если оценка уже есть — обновляем значение
            val existingRating = existingRatingOpt.get()
            existingRating.rating = request.rating
            existingRating.updatedAt = OffsetDateTime.now()
            ratingRepository.save(existingRating)
        } else {
            // Если нет — создаем новую (тут время поставится само в конструкторе/маппере)
            val newRating = request.toEntity(user, place)
            ratingRepository.save(newRating)
        }
    }
}