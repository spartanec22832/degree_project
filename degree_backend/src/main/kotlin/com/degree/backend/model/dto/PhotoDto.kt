package com.degree.backend.model.dto

data class PhotoDto(
    val id: Long,
    val url: String,
    val isMain: Boolean
)