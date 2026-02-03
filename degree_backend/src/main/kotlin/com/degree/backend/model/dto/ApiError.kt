package com.degree.backend.model.dto

import java.time.OffsetDateTime

data class ApiError(
    val code: String,
    val message: String,
    val details: Map<String, Any?>? = null,
    val path: String? = null,
    val timestamp: OffsetDateTime = OffsetDateTime.now()
)
