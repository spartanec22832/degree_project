package com.sfedu.degree_android.data.remote.api

import com.sfedu.degree_android.data.remote.dto.PlaceDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class FavoriteRequest(
    val placeId: Int
)

interface FavoritesApi {

    @POST("api/v1/interaction/favorite")
    suspend fun toggleFavorite(@Body request: FavoriteRequest)

    @GET("api/v1/interaction/favorite")
    suspend fun getUserFavorites(): List<PlaceDto>
}
