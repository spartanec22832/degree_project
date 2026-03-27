package com.sfedu.degree_android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_photos")
data class CachedPhotoEntity(
    @PrimaryKey
    val id: Long,
    val placeId: Int,
    val url: String,
    val isMain: Boolean
)