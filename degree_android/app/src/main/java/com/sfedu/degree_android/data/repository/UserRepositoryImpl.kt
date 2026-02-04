package com.sfedu.degree_android.data.repository

import com.sfedu.degree_android.data.remote.api.UserApi
import com.sfedu.degree_android.data.remote.dto.UserDto
import com.sfedu.degree_android.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {
    override suspend fun getProfile(): UserDto = api.profile()
}
