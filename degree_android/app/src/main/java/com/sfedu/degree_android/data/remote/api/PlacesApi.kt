package com.sfedu.degree_android.data.remote.api

import com.sfedu.degree_android.data.remote.dto.PlaceMapDto
import retrofit2.http.GET
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.data.remote.dto.PlaceSearchDto
import retrofit2.http.Path
import retrofit2.http.Query

interface PlacesApi {
    @GET("api/v1/places/map")
    suspend fun getMapPlaces(): List<PlaceMapDto>

    @GET("api/v1/places/{id}")
    suspend fun getPlace(@Path("id") id: Int): PlaceDto

    @GET("/api/v1/places/search")
    suspend fun searchPlaces(
        @Query("query") query: String
    ): List<PlaceSearchDto>
}