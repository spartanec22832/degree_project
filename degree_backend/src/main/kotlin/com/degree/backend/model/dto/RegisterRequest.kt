package com.degree.backend.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Логин обязателен")
    val username: String,

    @field:NotBlank(message = "Пароль обязателен")
    @field:Size(min = 6, message = "Пароль минимум 6 символов")
    val password: String
)