package com.sfedu.degree_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sfedu.degree_android.ui.components.AuthRequiredStub
import com.sfedu.degree_android.ui.screens.auth.AuthScreenHost
import com.sfedu.degree_android.ui.screens.favorites.FavoritesScreen
import com.sfedu.degree_android.ui.screens.map.MapScreen
import com.sfedu.degree_android.ui.screens.profile.ProfileScreen

@Composable
fun DegreeNavGraph(
    navController: NavHostController,
    token: String?,
    modifier: Modifier = Modifier,
    onRequireAuth: (targetRoute: String) -> Unit,
    onAuthSuccess: () -> Unit,
    onLogout: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = BottomDestination.Map.route,
        modifier = modifier
    ) {
        composable(BottomDestination.Map.route) {
            MapScreen()
        }

        composable(BottomDestination.Favorites.route) {
            if (token.isNullOrBlank()) {
                AuthRequiredStub(
                    title = "Избранное недоступно",
                    subtitle = "Авторизуйтесь, чтобы добавлять места в избранное",
                    buttonText = "Перейти в профиль",
                    onClick = { onRequireAuth(BottomDestination.Favorites.route) }
                )
            } else {
                FavoritesScreen()
            }
        }

        composable(BottomDestination.Profile.route) {
            if (token.isNullOrBlank()) {
                AuthScreenHost(onAuthSuccess = onAuthSuccess)
            } else {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}
