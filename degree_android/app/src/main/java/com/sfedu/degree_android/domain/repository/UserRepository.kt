package com.sfedu.degree_android.domain.repository

import com.sfedu.degree_android.data.remote.dto.UserDto

interface UserRepository {
    suspend fun getProfile(): UserDto
}
