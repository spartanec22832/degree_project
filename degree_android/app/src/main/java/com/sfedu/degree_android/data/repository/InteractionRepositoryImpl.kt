package com.sfedu.degree_android.data.repository

import com.sfedu.degree_android.data.remote.api.FavoritesApi
import com.sfedu.degree_android.data.remote.api.RatingRequest
import com.sfedu.degree_android.domain.repository.InteractionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InteractionRepositoryImpl @Inject constructor(
    private val api: FavoritesApi
) : InteractionRepository {

    override suspend fun setRating(placeId: Int, rating: Int) {
        api.setRating(
            RatingRequest(
                placeId = placeId,
                rating = rating.coerceIn(1, 5).toShort()
            )
        )
    }

    override suspend fun getMyRating(placeId: Int): Int {
        return (api.getMyRating(placeId)?.toInt()) ?: 0
    }
}
