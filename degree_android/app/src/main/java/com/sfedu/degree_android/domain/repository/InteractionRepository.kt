package com.sfedu.degree_android.domain.repository

interface InteractionRepository {
    suspend fun setRating(placeId: Int, rating: Int)
    suspend fun getMyRating(placeId: Int): Int
}
