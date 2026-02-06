package com.sfedu.degree_android.domain.repository

import com.sfedu.degree_android.data.remote.dto.PlaceMapDto
import com.sfedu.degree_android.data.remote.dto.PlaceDto

interface PlacesRepository {
    suspend fun getMapPlaces(): List<PlaceMapDto>
    suspend fun getPlace(id: Int): PlaceDto
}