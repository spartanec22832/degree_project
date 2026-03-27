package com.sfedu.degree_android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_places")
data class CachedPlaceEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: String,
    val category: String,
    val description: String?,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
    val worktime: String?,
    val price: String?,
    val contactPhone: String?,
    val contactLink: String?,
    val isFavorite: Boolean,
    val averageRating: Double?
)