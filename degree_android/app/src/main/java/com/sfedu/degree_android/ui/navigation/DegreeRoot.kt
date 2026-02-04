package com.sfedu.degree_android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun DegreeRoot(
    token: String?,
    onLogout: () -> Unit,
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Куда пользователь хотел попасть, но упёрся в авторизацию
    var pendingRoute by rememberSaveable { mutableStateOf<String?>(null) }

    fun openTab(route: String) {
        // ВАЖНО: любое ручное действие по табу сбрасывает старый pending
        pendingRoute = null

        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                // ВАЖНО: выключаем save/restore — они и давали “магические” возвраты
                saveState = false
            }
            launchSingleTop = true
            restoreState = false
        }
    }

    fun requireAuthThenGo(targetRoute: String) {
        pendingRoute = targetRoute
        navController.navigate(BottomDestination.Profile.route) {
            launchSingleTop = true
        }
    }

    fun onAuthSuccess() {
        val target = pendingRoute
        pendingRoute = null

        if (target != null) {
            navController.navigate(target) {
                launchSingleTop = true
                // Чтоб после логина не “откатывало” обратно в профиль
                popUpTo(BottomDestination.Profile.route) { inclusive = true }
            }
        }
    }

    // Страховка: если токен появился, а мы на профиле и есть pending — уходим в цель
    LaunchedEffect(token, currentRoute, pendingRoute) {
        val target = pendingRoute
        if (!token.isNullOrBlank() &&
            target != null &&
            currentRoute == BottomDestination.Profile.route
        ) {
            pendingRoute = null
            navController.navigate(target) {
                launchSingleTop = true
                popUpTo(BottomDestination.Profile.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            DegreeBottomBar(
                currentRoute = currentRoute,
                onSelectTab = { dest -> openTab(dest.route) }
            )
        }
    ) { padding ->
        DegreeNavGraph(
            navController = navController,
            token = token,
            modifier = Modifier.padding(padding),
            onRequireAuth = ::requireAuthThenGo,
            onAuthSuccess = ::onAuthSuccess,
            onLogout = {
                pendingRoute = null
                onLogout()
            }
        )
    }
}
