package com.sfedu.degree_android.domain.repository

import com.sfedu.degree_android.data.remote.dto.PlaceMapDto
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.data.remote.dto.PlaceSearchDto

interface PlacesRepository {
    suspend fun getMapPlaces(): List<PlaceMapDto>
    suspend fun getPlace(id: Int): PlaceDto
    suspend fun searchPlaces(query: String): List<PlaceSearchDto>
    suspend fun warmUpPlacesCache(placeIds: List<Int>)
}