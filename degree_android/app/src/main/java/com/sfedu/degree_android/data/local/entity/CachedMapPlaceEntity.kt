package com.sfedu.degree_android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_map_places")
data class CachedMapPlaceEntity(
    @PrimaryKey
    val id: Int,
    val type: String,
    val latitude: Double,
    val longitude: Double
)