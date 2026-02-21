package com.sfedu.degree_android.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.degree_android.data.remote.dto.PlaceSearchDto
import com.sfedu.degree_android.domain.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapSearchViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    data class UiState(
        val expanded: Boolean = false,
        val query: String = "",
        val loading: Boolean = false,
        val results: List<PlaceSearchDto> = emptyList(),
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(350)
                .map { it.trim() }
                .distinctUntilChanged()
                .collectLatest { q ->
                    _state.update { it.copy(query = q, error = null) }

                    if (q.length < 2) {
                        _state.update { it.copy(loading = false, results = emptyList()) }
                        return@collectLatest
                    }

                    _state.update { it.copy(loading = true, error = null) }
                    try {
                        val res = placesRepository.searchPlaces(q)
                        _state.update { it.copy(loading = false, results = res) }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(
                                loading = false,
                                results = emptyList(),
                                error = e.message ?: "Ошибка поиска"
                            )
                        }
                    }
                }
        }
    }

    fun toggleExpanded() {
        val newValue = !_state.value.expanded
        _state.update { it.copy(expanded = newValue, error = null) }
        if (!newValue) setQuery("")
    }

    fun collapse() {
        _state.update { it.copy(expanded = false, error = null) }
        setQuery("")
    }

    fun setQuery(text: String) {
        _state.update { it.copy(query = text, error = null) }
        queryFlow.value = text
    }
}