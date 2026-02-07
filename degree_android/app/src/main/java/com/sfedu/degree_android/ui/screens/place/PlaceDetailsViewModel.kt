package com.sfedu.degree_android.ui.screens.place

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.domain.repository.InteractionRepository
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
    private val repo: PlacesRepository,
    private val interactionRepo: InteractionRepository
) : ViewModel() {

    var state by mutableStateOf(PlaceDetailsUiState())
        private set

    var ratingSubmitting by mutableStateOf(false)
        private set

    var myRating by mutableStateOf(0)
        private set

    fun load(id: Int) {
        state = state.copy(loading = true, error = null)
        viewModelScope.launch {
            runCatching { repo.getPlace(id) }
                .onSuccess { place ->
                    state = state.copy(place = place, loading = false)

                    // подтягиваем myRating только если юзер авторизован
                    // (если у тебя токен хранится в interceptor и 401 ловится — ок)
                    runCatching { interactionRepo.getMyRating(id) }
                        .onSuccess { myRating = it }
                        .onFailure { myRating = 0 } // на 401/ошибке просто 0
                }
                .onFailure { e ->
                    state = state.copy(loading = false, error = e.message)
                }
        }
    }

    fun setRating(placeId: Int, rating: Int, onError: (String) -> Unit) {
        if (ratingSubmitting) return
        ratingSubmitting = true
        viewModelScope.launch {
            runCatching { interactionRepo.setRating(placeId, rating) }
                .onSuccess {
                    myRating = rating.coerceIn(1, 5)
                    load(placeId)
                }
                .onFailure { e ->
                    onError(e.message ?: "Не удалось отправить оценку")
                }
            ratingSubmitting = false
        }
    }
}
