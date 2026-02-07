package com.sfedu.degree_android.data.repository

import com.sfedu.degree_android.core.datastore.TokenStorage
import com.sfedu.degree_android.data.remote.api.AuthApi
import com.sfedu.degree_android.data.remote.api.ChangePasswordRequest
import com.sfedu.degree_android.data.remote.dto.AuthRequest
import com.sfedu.degree_android.data.remote.dto.RegisterRequest
import com.sfedu.degree_android.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(login: String, password: String) {
        val res = api.authenticate(AuthRequest(username = login, password = password))
        tokenStorage.saveAccessToken(res.accessToken)
    }

    override suspend fun register(login: String, password: String) {
        val res = api.register(RegisterRequest(username = login, password = password))
        tokenStorage.saveAccessToken(res.accessToken)
    }

    override suspend fun logout() {
        tokenStorage.clear()
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmationPassword: String
    ) {
        api.changePassword(
            ChangePasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword,
                confirmationPassword = confirmationPassword
            )
        )
    }
}
