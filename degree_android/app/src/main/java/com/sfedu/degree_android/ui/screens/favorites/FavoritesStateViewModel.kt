package com.sfedu.degree_android.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.degree_android.core.network.ApiErrorParser
import com.sfedu.degree_android.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FavoritesStateViewModel @Inject constructor(
    private val repo: FavoritesRepository
) : ViewModel() {

    val favoriteIds = repo.favoriteIds
    val favorites = repo.favorites

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun clearError() { _error.value = null }

    fun refresh() {
        viewModelScope.launch {
            runCatching { repo.refresh() }
                .onFailure { e -> _error.value = humanError(e) }
        }
    }

    fun toggle(placeId: Int) {
        viewModelScope.launch {
            runCatching { repo.toggle(placeId) }
                .onFailure { e -> _error.value = humanError(e) }
        }
    }

    private fun humanError(e: Throwable): String {
        return when (e) {
            is HttpException -> ApiErrorParser.humanMessage(e) // у тебя уже есть единый парсер
            is IOException -> "Сеть недоступна"
            else -> e.message ?: "Ошибка"
        }
    }
}
