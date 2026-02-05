package com.sfedu.degree_android.ui.screens.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.degree_android.data.remote.dto.PlaceMapDto
import com.sfedu.degree_android.domain.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapUiState(
    val loading: Boolean = false,
    val places: List<PlaceMapDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    var state by mutableStateOf(MapUiState())
        private set

    fun load() {
        if (state.loading) return
        state = state.copy(loading = true, error = null)

        viewModelScope.launch {
            runCatching { placesRepository.getMapPlaces() }
                .onSuccess { state = state.copy(loading = false, places = it) }
                .onFailure { state = state.copy(loading = false, error = it.message ?: "Ошибка загрузки") }
        }
    }
}
