package com.sfedu.degree_android.data.mapper

import com.sfedu.degree_android.data.local.entity.CachedMapPlaceEntity
import com.sfedu.degree_android.data.local.entity.CachedPhotoEntity
import com.sfedu.degree_android.data.local.entity.CachedPlaceEntity
import com.sfedu.degree_android.data.remote.dto.PhotoDto
import com.sfedu.degree_android.data.remote.dto.PlaceDto
import com.sfedu.degree_android.data.remote.dto.PlaceMapDto
import com.sfedu.degree_android.data.remote.dto.PlaceSearchDto
import java.math.BigDecimal

fun PlaceMapDto.toCachedEntity(): CachedMapPlaceEntity =
    CachedMapPlaceEntity(
        id = id,
        type = type,
        latitude = latitude,
        longitude = longitude
    )

fun CachedMapPlaceEntity.toDto(): PlaceMapDto =
    PlaceMapDto(
        id = id,
        type = type,
        latitude = latitude,
        longitude = longitude
    )

fun PlaceDto.toCachedEntity(): CachedPlaceEntity =
    CachedPlaceEntity(
        id = id,
        name = name,
        type = type,
        category = category,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        worktime = worktime,
        price = price?.toPlainString(),
        contactPhone = contactPhone,
        contactLink = contactLink,
        isFavorite = isFavorite,
        averageRating = averageRating
    )

fun CachedPlaceEntity.toDto(photos: List<PhotoDto>): PlaceDto =
    PlaceDto(
        id = id,
        name = name,
        type = type,
        category = category,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        worktime = worktime,
        price = price?.let { BigDecimal(it) },
        contactPhone = contactPhone,
        contactLink = contactLink,
        photos = photos,
        isFavorite = isFavorite,
        averageRating = averageRating
    )

fun PhotoDto.toCachedEntity(placeId: Int): CachedPhotoEntity =
    CachedPhotoEntity(
        id = id,
        placeId = placeId,
        url = url,
        isMain = isMain
    )

fun CachedPhotoEntity.toDto(): PhotoDto =
    PhotoDto(
        id = id,
        url = url,
        isMain = isMain
    )

fun CachedPlaceEntity.toSearchDto(): PlaceSearchDto =
    PlaceSearchDto(
        id = id,
        name = name,
        type = type,
        category = category
    )