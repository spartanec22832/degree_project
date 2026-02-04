package com.sfedu.degree_android.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sfedu.degree_android.data.remote.dto.UserDto
import com.sfedu.degree_android.domain.repository.AuthRepository
import com.sfedu.degree_android.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class ProfileUiState(
    val loading: Boolean = true,
    val user: UserDto? = null,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(ProfileUiState())
        private set

    fun load() {
        state = state.copy(loading = true, error = null)
        viewModelScope.launch {
            state = try {
                val me = userRepository.getProfile()
                ProfileUiState(loading = false, user = me)
            } catch (e: HttpException) {
                ProfileUiState(loading = false, error = "HTTP ${e.code()}")
            } catch (e: IOException) {
                ProfileUiState(loading = false, error = "Сеть недоступна")
            } catch (e: Exception) {
                ProfileUiState(loading = false, error = "Неизвестная ошибка")
            }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onDone()
        }
    }
}
