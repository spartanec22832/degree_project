package com.sfedu.degree_android.data.remote.api

import com.sfedu.degree_android.data.remote.dto.PlaceMapDto
import retrofit2.http.GET
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import retrofit2.http.Path

interface PlacesApi {
    @GET("api/v1/places/map")
    suspend fun getMapPlaces(): List<PlaceMapDto>

    @GET("api/v1/places/{id}")
    suspend fun getPlace(@Path("id") id: Int): PlaceDto
}