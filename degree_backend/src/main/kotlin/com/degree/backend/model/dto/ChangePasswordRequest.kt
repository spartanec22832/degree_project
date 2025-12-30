package com.degree.backend.model.dto

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmationPassword: String
)