package com.sfedu.degree_android.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.degree_android.core.network.ApiErrorParser
import com.sfedu.degree_android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(AuthUiState())
        private set

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        state = state.copy(loading = true, error = null)

        viewModelScope.launch {
            try {
                authRepository.login(username.trim(), password)
                state = state.copy(loading = false, error = null)
                onSuccess()
            } catch (e: HttpException) {
                state = state.copy(
                    loading = false,
                    error = ApiErrorParser.humanMessage(e)
                )
            } catch (e: IOException) {
                state = state.copy(
                    loading = false,
                    error = "Ошибка сети"
                )
            } catch (e: Exception) {
                state = state.copy(
                    loading = false,
                    error = e.message ?: "Произошла непредвиденная ошибка"
                )
            }
        }
    }

    fun register(username: String, password: String, onSuccess: () -> Unit) {
        state = state.copy(loading = true, error = null)

        viewModelScope.launch {
            try {
                authRepository.register(username.trim(), password)
                state = state.copy(loading = false, error = null)
                onSuccess()
            } catch (e: HttpException) {
                state = state.copy(
                    loading = false,
                    error = ApiErrorParser.humanMessage(e)
                )
            } catch (e: IOException) {
                state = state.copy(
                    loading = false,
                    error = "Не удалось подключиться к серверу. Проверьте интернет-соединение."
                )
            } catch (e: Exception) {
                state = state.copy(
                    loading = false,
                    error = e.message ?: "Произошла непредвиденная ошибка"
                )
            }
        }
    }

    fun clearError() {
        if (state.error != null) {
            state = state.copy(error = null)
        }
    }
}
