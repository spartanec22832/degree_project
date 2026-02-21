package com.sfedu.degree_android.data.repository

import com.sfedu.degree_android.data.remote.api.PlacesApi
import com.sfedu.degree_android.data.remote.dto.PlaceMapDto
import com.sfedu.degree_android.domain.repository.PlacesRepository
import javax.inject.Inject
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.data.remote.dto.PlaceSearchDto

class PlacesRepositoryImpl @Inject constructor(
    private val api: PlacesApi
) : PlacesRepository {
    override suspend fun getMapPlaces(): List<PlaceMapDto> = api.getMapPlaces()
    override suspend fun getPlace(id: Int): PlaceDto = api.getPlace(id)
    override suspend fun searchPlaces(query: String): List<PlaceSearchDto> =
        api.searchPlaces(query)
}