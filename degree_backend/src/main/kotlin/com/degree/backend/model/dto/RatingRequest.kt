package com.degree.backend.model.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class RatingRequest(
    @field:Min(1, message = "placeId должен быть > 0")
    val placeId: Int,

    @field:Min(1, message = "rating должен быть 1..5")
    @field:Max(5, message = "rating должен быть 1..5")
    val rating: Short
)