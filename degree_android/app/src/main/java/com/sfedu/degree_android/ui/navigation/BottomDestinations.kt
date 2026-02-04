package com.sfedu.degree_android.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Map : BottomDestination("tab_map", "Карта", Icons.Filled.Map)
    data object Favorites : BottomDestination("tab_favorites", "Избранное", Icons.Filled.Favorite)
    data object Profile : BottomDestination("tab_profile", "Профиль", Icons.Filled.Person)
}

val bottomDestinations = listOf(
    BottomDestination.Map,
    BottomDestination.Favorites,
    BottomDestination.Profile,
)