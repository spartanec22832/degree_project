package com.sfedu.degree_android.ui.screens.place

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

data class PlaceDetailsUiState(
    val loading: Boolean = false,
    val place: PlaceDto? = null,
    val error: String? = null
)

@HiltViewModel
class PlaceDetailsViewModel @Inject constructor(
    private val repo: PlacesRepository
) : ViewModel() {

    var state by mutableStateOf(PlaceDetailsUiState())
        private set

    fun load(id: Int) {
        if (state.loading) return
        state = PlaceDetailsUiState(loading = true)
        viewModelScope.launch {
            runCatching { repo.getPlace(id) }
                .onSuccess { state = PlaceDetailsUiState(place = it) }
                .onFailure { state = PlaceDetailsUiState(error = it.message ?: "Ошибка") }
        }
    }
}
