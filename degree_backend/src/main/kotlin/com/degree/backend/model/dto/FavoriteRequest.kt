package com.degree.backend.model.dto

import jakarta.validation.constraints.Min

data class FavoriteRequest(
    @field:Min(1, message = "placeId должен быть > 0")
    val placeId: Int
)