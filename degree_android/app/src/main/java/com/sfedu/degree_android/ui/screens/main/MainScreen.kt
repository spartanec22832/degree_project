package com.sfedu.degree_android.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sfedu.degree_android.ui.navigation.DegreeRoot
import com.sfedu.degree_android.ui.session.SessionViewModel

@Composable
fun MainScreen() {
    val sessionVm: SessionViewModel = hiltViewModel()
    val token = sessionVm.token.collectAsStateWithLifecycle().value

    DegreeRoot(
        token = token,
        onLogout = { sessionVm.logout() }
    )
}
