package com.degree.backend.model.mapper

import com.degree.backend.model.dto.PhotoDto
import com.degree.backend.model.dto.PlaceDto
import com.degree.backend.model.dto.PlaceMapDto
import com.degree.backend.model.entity.Photo
import com.degree.backend.model.entity.Place

// фото
fun Photo.toDto() = PhotoDto(
    id = this.id,
    url = this.url,
    isMain = this.isMain
)

// dto под карту
fun Place.toMapDto() = PlaceMapDto(
    id = this.id,
    type = this.type ?: "unknown", // Защита от null, если в базе вдруг пусто
    latitude = this.latitude ?: 0.0,
    longitude = this.longitude ?: 0.0
)

// для витрин
fun Place.toDto(
    isFavorite: Boolean,
    averageRating: Double?,
    placePhotos: List<Photo>
) = PlaceDto(
    id = this.id,
    name = this.name ?: "",
    type = this.type ?: "",
    category = this.category ?: "",
    description = this.description,
    address = this.address,
    latitude = this.latitude ?: 0.0,
    longitude = this.longitude ?: 0.0,
    worktime = this.workTime,
    price = this.price,
    contactPhone = this.contactPhone,
    contactLink = this.contactLink,

    photos = placePhotos.map { it.toDto() },

    isFavorite = isFavorite,
    averageRating = averageRating
)