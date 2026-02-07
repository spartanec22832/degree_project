package com.sfedu.degree_android.data.repository

import com.sfedu.degree_android.data.remote.api.FavoriteRequest
import com.sfedu.degree_android.data.remote.api.FavoritesApi
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val api: FavoritesApi
) : FavoritesRepository {

    private val _favorites = MutableStateFlow<List<PlaceDto>>(emptyList())
    override val favorites: StateFlow<List<PlaceDto>> = _favorites

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    override val favoriteIds: StateFlow<Set<Int>> = _favoriteIds

    override suspend fun refresh() {
        val list = api.getUserFavorites()
        _favorites.value = list
        _favoriteIds.value = list.map { it.id }.toSet()
    }

    override suspend fun toggle(placeId: Int) {
        api.toggleFavorite(FavoriteRequest(placeId))
        // важный момент: после toggle — подтягиваем актуальное состояние с бэка
        refresh()
    }
}
