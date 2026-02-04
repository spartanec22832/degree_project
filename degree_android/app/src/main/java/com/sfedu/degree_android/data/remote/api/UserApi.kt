package com.sfedu.degree_android.data.remote.api

import com.sfedu.degree_android.data.remote.dto.UserDto
import retrofit2.http.GET

interface UserApi {
    @GET("api/v1/user/profile")
    suspend fun profile(): UserDto
}
