package com.sfedu.degree_android.data.remote.api

import com.sfedu.degree_android.data.remote.dto.PlaceMapDto
import retrofit2.http.GET

interface PlacesApi {
    @GET("api/v1/places/map")
    suspend fun getMapPlaces(): List<PlaceMapDto>
}