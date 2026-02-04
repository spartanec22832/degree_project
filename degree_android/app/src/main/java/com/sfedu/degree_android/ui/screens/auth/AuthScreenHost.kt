package com.sfedu.degree_android.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthScreenHost(
    onAuthSuccess: () -> Unit,
    vm: AuthViewModel = hiltViewModel()
) {
    var isLogin by remember { mutableStateOf(true) }

    if (isLogin) {
        LoginScreen(
            state = vm.state,
            onLogin = { u, p -> vm.login(u, p, onSuccess = onAuthSuccess) },
            onSwitchToRegister = { isLogin = false }
        )
    } else {
        RegisterScreen(
            state = vm.state,
            onRegister = { u, p -> vm.register(u, p, onSuccess = onAuthSuccess) },
            onSwitchToLogin = { isLogin = true }
        )
    }
}
