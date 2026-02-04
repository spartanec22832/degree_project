package com.sfedu.degree_android.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.sfedu.degree_android.ui.components.AuthRequiredStub
import com.sfedu.degree_android.ui.navigation.BottomDestination
import com.sfedu.degree_android.ui.navigation.bottomDestinations
import com.sfedu.degree_android.ui.screens.auth.AuthScreenHost
import com.sfedu.degree_android.ui.screens.favorites.FavoritesScreen
import com.sfedu.degree_android.ui.screens.map.MapScreen
import com.sfedu.degree_android.ui.screens.profile.ProfileScreen
import com.sfedu.degree_android.ui.screens.profile.ProfileViewModel
import com.sfedu.degree_android.ui.session.SessionViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val sessionVm: SessionViewModel = hiltViewModel()
    val token by sessionVm.token.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Запоминаем куда пользователь хотел попасть, но упёрся в авторизацию
    var pendingRoute by rememberSaveable { mutableStateOf<String?>(null) }

    // Страховка: если токен появился, а мы всё ещё на профиле и есть pending —
    // автоматически переходим туда, куда хотели
    LaunchedEffect(token, currentRoute) {
        val target = pendingRoute
        if (!token.isNullOrBlank() &&
            target != null &&
            currentRoute == BottomDestination.Profile.route
        ) {
            pendingRoute = null
            navController.navigate(target) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomDestinations.forEach { dest ->
                    NavigationBarItem(
                        selected = currentRoute == dest.route,
                        onClick = {
                            // ВАЖНО: никаких редиректов из bottom bar по токену!
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
            composable(BottomDestination.Map.route) {
                MapScreen()
            }

            composable(BottomDestination.Favorites.route) {
                if (token.isNullOrBlank()) {
                    AuthRequiredStub(
                        title = "Избранное недоступно",
                        subtitle = "Войди, чтобы сохранять места",
                        buttonText = "Перейти в профиль",
                        onClick = {
                            // запоминаем цель и уводим в профиль
                            pendingRoute = BottomDestination.Favorites.route
                            navController.navigate(BottomDestination.Profile.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                } else {
                    FavoritesScreen()
                }
            }

            composable(BottomDestination.Profile.route) {
                if (token.isNullOrBlank()) {
                    AuthScreenHost(
                        onAuthSuccess = {
                            // Если пришли из другой вкладки — возвращаем туда
                            val target = pendingRoute
                            pendingRoute = null
                            if (target != null) {
                                navController.navigate(target) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            // Если target == null — просто останемся в профиле
                        }
                    )
                } else {
                    // VM создаём только когда токен уже есть
                    val profileVm: ProfileViewModel = hiltViewModel()
                    ProfileScreen(
                        vm = profileVm,
                        onLogout = {
                            // при логауте чистим pending и остаёмся в профиле (там покажется Auth)
                            pendingRoute = null
                            sessionVm.logout()
                        }
                    )
                }
            }
        }
    }
}
