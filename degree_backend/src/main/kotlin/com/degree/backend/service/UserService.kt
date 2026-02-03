package com.degree.backend.service

import com.degree.backend.model.dto.UserDto
import com.degree.backend.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository
) {

    // Метод для получения профиля текущего пользователя
    fun getUserProfile(username: String): UserDto {
        val user = userRepository.findByLogin(username)
            .orElseThrow { RuntimeException("User not found") }

        // Превращаем Entity в безопасный DTO
        return UserDto(
            id = user.id,
            username = user.username,
            createdAt = user.createdAt
        )
    }
}