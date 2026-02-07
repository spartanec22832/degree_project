package com.sfedu.degree_android.domain.repository

interface AuthRepository {
    suspend fun login(login: String, password: String)
    suspend fun register(login: String, password: String)
    suspend fun logout()
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmationPassword: String
    )
}
