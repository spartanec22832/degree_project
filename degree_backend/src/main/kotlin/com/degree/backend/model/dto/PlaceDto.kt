package com.degree.backend.model.dto

import java.math.BigDecimal

data class PlaceDto(
    val id: Int,
    val name: String,
    val type: String,
    val category: String,
    val description: String?,

    val address: String?,
    val latitude: Double,
    val longitude: Double,

    // Детали
    val worktime: String?,
    val price: BigDecimal?,
    val contactPhone: String?,
    val contactLink: String?,

    val photos: List<PhotoDto> = emptyList(),

    val isFavorite: Boolean = false,
    val averageRating: Double? = null
)