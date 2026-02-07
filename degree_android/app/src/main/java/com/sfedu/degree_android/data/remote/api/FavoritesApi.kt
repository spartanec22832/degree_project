package com.sfedu.degree_android.data.remote.api

import com.sfedu.degree_android.data.remote.dto.PlaceDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class FavoriteRequest(val placeId: Int)
data class RatingRequest(val placeId: Int, val rating: Short)

interface FavoritesApi {

    @POST("api/v1/interaction/favorite")
    suspend fun toggleFavorite(@Body request: FavoriteRequest)

    @GET("api/v1/interaction/favorite")
    suspend fun getUserFavorites(): List<PlaceDto>

    @POST("api/v1/interaction/rating")
    suspend fun setRating(@Body request: RatingRequest)

    @GET("api/v1/interaction/rating/{placeId}")
    suspend fun getMyRating(@Path("placeId") placeId: Int): Short?

}