package com.sfedu.degree_android.ui.screens.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.domain.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlacePreviewUiState(
    val loading: Boolean = false,
    val place: PlaceDto? = null,
    val error: String? = null
)

@HiltViewModel
class PlacePreviewViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    var state by mutableStateOf(PlacePreviewUiState())
        private set

    fun load(placeId: Int) {
        state = PlacePreviewUiState(loading = true)
        viewModelScope.launch {
            runCatching { placesRepository.getPlace(placeId) }
                .onSuccess { state = PlacePreviewUiState(place = it) }
                .onFailure { state = PlacePreviewUiState(error = it.message ?: "Ошибка") }
        }
    }

    fun clear() {
        state = PlacePreviewUiState()
    }
}
