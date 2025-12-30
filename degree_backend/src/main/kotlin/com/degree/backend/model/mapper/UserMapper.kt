package com.degree.backend.model.mapper

import com.degree.backend.model.dto.RegisterRequest
import com.degree.backend.model.entity.User
import java.time.OffsetDateTime

fun RegisterRequest.toEntity(encryptedPassword: String) = User(
    id = 0,
    username = this.username,
    passwordEncrypted = encryptedPassword,
    createdAt = OffsetDateTime.now()
)