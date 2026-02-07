package com.sfedu.degree_android.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.degree_android.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesStateViewModel @Inject constructor(
    private val repo: FavoritesRepository
) : ViewModel() {

    val favoriteIds = repo.favoriteIds
    val favorites = repo.favorites

    fun refresh() {
        viewModelScope.launch { repo.refresh() }
    }

    fun toggle(placeId: Int) {
        viewModelScope.launch { repo.toggle(placeId) }
    }
}

