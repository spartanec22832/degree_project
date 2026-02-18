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

enum class MapPlaceType(val title: String) {
    FOOD("Еда"),
    PLACES("Места"),
    HOTELS("Гостиницы")
}

data class MapUiState(
    val loading: Boolean = false,
    val places: List<PlaceMapDto> = emptyList(),
    val enabledTypes: Set<MapPlaceType> = setOf(
        MapPlaceType.FOOD,
        MapPlaceType.PLACES,
        MapPlaceType.HOTELS
    ),
    val bufferEnabled: Boolean = false,
    val bufferRadiusMeters: Float = 1000f, // по умолчанию 1 км

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

    fun toggleType(type: MapPlaceType) {
        val current = state.enabledTypes
        val next =
            if (current.contains(type)) current - type
            else current + type

        // чтобы пользователь не мог снять вообще всё (опционально, но удобно)
        state = state.copy(enabledTypes = if (next.isEmpty()) current else next)
    }

    fun setBufferEnabled(enabled: Boolean) {
        state = state.copy(bufferEnabled = enabled)
    }

    fun setBufferRadiusMeters(value: Float) {
        val minR = 100f
        val maxR = 3000f
        val step = 50f

        val clamped = value.coerceIn(minR, maxR)
        val snapped = (kotlin.math.round(clamped / step) * step).coerceIn(minR, maxR)

        state = state.copy(bufferRadiusMeters = snapped)
    }
}
