package com.sfedu.degree_android.ui.screens.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesStateViewModel @Inject constructor() : ViewModel() {

    // Заглушка. Потом заменим на репозиторий + бэк.
    var favoriteIds by mutableStateOf(setOf<Int>())
        private set

    fun isFavorite(id: Int): Boolean = id in favoriteIds

    fun toggle(id: Int) {
        favoriteIds = if (id in favoriteIds) favoriteIds - id else favoriteIds + id
    }
}
