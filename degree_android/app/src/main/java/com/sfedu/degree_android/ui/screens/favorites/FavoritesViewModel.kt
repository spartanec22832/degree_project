package com.sfedu.degree_android.ui.screens.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.domain.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val loading: Boolean = false,
    val items: List<PlaceDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    var state by mutableStateOf(FavoritesUiState())
        private set

    fun load(ids: Set<Int>) {
        if (ids.isEmpty()) {
            state = FavoritesUiState(items = emptyList())
            return
        }

        state = FavoritesUiState(loading = true)

        viewModelScope.launch {
            runCatching {
                ids.toList().map { id ->
                    async { placesRepository.getPlace(id) }
                }.awaitAll()
            }.onSuccess { places ->
                state = FavoritesUiState(items = places)
            }.onFailure { e ->
                state = FavoritesUiState(error = e.message ?: "Ошибка загрузки избранного")
            }
        }
    }
}
