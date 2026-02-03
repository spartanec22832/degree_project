package com.degree.backend.model.dto

import jakarta.validation.constraints.NotBlank

data class AuthenticationRequest(
    @field:NotBlank(message = "Логин обязателен")
    val username: String,

    @field:NotBlank(message = "Пароль обязателен")
    val password: String
)