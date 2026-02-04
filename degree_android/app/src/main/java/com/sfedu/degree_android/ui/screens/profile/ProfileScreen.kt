package com.sfedu.degree_android.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    vm: ProfileViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { vm.load() }

    val state = vm.state

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Профиль")

        when {
            state.loading -> Text("Загрузка...")
            state.error != null -> Text("Ошибка: ${state.error}")
            state.user != null -> {
                Text("ID: ${state.user.id}")
                Text("Username: ${state.user.username}")
                Text("CreatedAt: ${state.user.createdAt}")
            }
        }

        Button(onClick = { vm.load() }) {
            Text("Обновить профиль")
        }

        Button(onClick = { vm.logout(onLogout) }) {
            Text("Выйти")
        }
    }
}
