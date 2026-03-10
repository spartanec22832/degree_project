package com.sfedu.degree_android.data.remote.api

import com.sfedu.degree_android.data.remote.dto.AuthRequest
import com.sfedu.degree_android.data.remote.dto.AuthResponse
import com.sfedu.degree_android.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmationPassword: String
)

interface AuthApi {
    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("api/v1/auth/authenticate")
    suspend fun authenticate(@Body body: AuthRequest): AuthResponse

    @PATCH("api/v1/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest)

    @POST("api/v1/auth/logout")
    suspend fun logout()

    @POST("api/v1/auth/logout-all")
    suspend fun logoutAll()
}
