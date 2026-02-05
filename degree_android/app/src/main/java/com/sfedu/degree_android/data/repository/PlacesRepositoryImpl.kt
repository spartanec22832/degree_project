package com.sfedu.degree_android.data.repository

import com.sfedu.degree_android.data.remote.api.PlacesApi
import com.sfedu.degree_android.data.remote.dto.PlaceMapDto
import com.sfedu.degree_android.domain.repository.PlacesRepository
import javax.inject.Inject

class PlacesRepositoryImpl @Inject constructor(
    private val api: PlacesApi
) : PlacesRepository {
    override suspend fun getMapPlaces(): List<PlaceMapDto> = api.getMapPlaces()
}