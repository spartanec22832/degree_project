package com.sfedu.degree_android.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sfedu.degree_android.ui.navigation.BottomDestination
import com.sfedu.degree_android.ui.navigation.bottomDestinations
import com.sfedu.degree_android.ui.screens.favorites.FavoritesScreen
import com.sfedu.degree_android.ui.screens.map.MapScreen
import com.sfedu.degree_android.ui.screens.profile.ProfileScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomDestinations.forEach { dest ->
                    val selected = currentRoute == dest.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomDestination.Map.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomDestination.Map.route) { MapScreen(contentPadding = padding) }
            composable(BottomDestination.Favorites.route) { FavoritesScreen() }
            composable(BottomDestination.Profile.route) { ProfileScreen() }
        }
    }
}