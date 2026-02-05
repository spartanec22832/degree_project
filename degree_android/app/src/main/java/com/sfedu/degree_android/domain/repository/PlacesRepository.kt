package com.sfedu.degree_android.domain.repository

import com.sfedu.degree_android.data.remote.dto.PlaceMapDto

interface PlacesRepository {
    suspend fun getMapPlaces(): List<PlaceMapDto>
}