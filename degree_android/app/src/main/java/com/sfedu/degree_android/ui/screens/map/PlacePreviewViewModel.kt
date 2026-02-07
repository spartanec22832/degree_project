package com.sfedu.degree_android.ui.screens.map

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

data class PlacePreviewUiState(
    val loading: Boolean = false,
    val place: PlaceDto? = null,
    val error: String? = null
)

@HiltViewModel
class PlacePreviewViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val interactionRepo: InteractionRepository
) : ViewModel() {

    var state by mutableStateOf(PlacePreviewUiState())
        private set

    var myRating by mutableStateOf(0)
        private set

    var ratingSubmitting by mutableStateOf(false)
        private set

    fun load(placeId: Int) {
        state = PlacePreviewUiState(loading = true)

        viewModelScope.launch {
            runCatching { placesRepository.getPlace(placeId) }
                .onSuccess { place ->
                    state = PlacePreviewUiState(place = place)

                    // ✅ подтянуть мою оценку (если не авторизован — бэк вернет 401, мы просто поставим 0)
                    runCatching { interactionRepo.getMyRating(placeId) }
                        .onSuccess { myRating = it }
                        .onFailure { myRating = 0 }
                }
                .onFailure { e ->
                    state = PlacePreviewUiState(error = e.message ?: "Ошибка")
                    myRating = 0
                }
        }
    }

    fun clear() {
        state = PlacePreviewUiState()
        myRating = 0
        ratingSubmitting = false
    }

    fun setRating(placeId: Int, rating: Int, onError: (String) -> Unit) {
        if (ratingSubmitting) return
        ratingSubmitting = true

        viewModelScope.launch {
            runCatching { interactionRepo.setRating(placeId, rating) }
                .onSuccess {
                    // ✅ мгновенно подсветим звезды
                    myRating = rating.coerceIn(1, 5)
                    // ✅ обновим averageRating в превью
                    load(placeId)
                }
                .onFailure { onError(it.message ?: "Не удалось отправить оценку") }

            ratingSubmitting = false
        }
    }
}
