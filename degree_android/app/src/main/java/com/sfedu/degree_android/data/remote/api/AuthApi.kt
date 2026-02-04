package com.sfedu.degree_android.data.remote.api

import com.sfedu.degree_android.data.remote.dto.AuthRequest
import com.sfedu.degree_android.data.remote.dto.AuthResponse
import com.sfedu.degree_android.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("api/v1/auth/authenticate")
    suspend fun authenticate(@Body body: AuthRequest): AuthResponse
}
