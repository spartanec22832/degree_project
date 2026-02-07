package com.sfedu.degree_android.domain.repository

import com.sfedu.degree_android.data.remote.dto.PlaceDto
import kotlinx.coroutines.flow.StateFlow

interface FavoritesRepository {
    val favorites: StateFlow<List<PlaceDto>>
    val favoriteIds: StateFlow<Set<Int>>

    suspend fun refresh()
    suspend fun toggle(placeId: Int)
}
