package com.degree.backend.model.dto

import java.time.OffsetDateTime

// P.s.: username == login
data class UserDto(
    val id: Long,
    val username: String,
    val createdAt: OffsetDateTime
)