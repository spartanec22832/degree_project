package com.degree.backend.model.mapper

import com.degree.backend.model.dto.FavoriteRequest
import com.degree.backend.model.dto.RatingRequest
import com.degree.backend.model.entity.Favorite
import com.degree.backend.model.entity.Place
import com.degree.backend.model.entity.Rating
import com.degree.backend.model.entity.User
import java.time.OffsetDateTime


fun FavoriteRequest.toEntity(user: User, place: Place): Favorite {
    require(this.placeId == place.id) { "Place ID mismatch" }
    return Favorite(
        user = user,
        place = place,
        addedAt = OffsetDateTime.now()
    )
}

fun RatingRequest.toEntity(user: User, place: Place): Rating {
    require(this.placeId == place.id) { "Place ID mismatch" }
    return Rating(
        user = user,
        place = place,
        rating = this.rating,
        updatedAt = OffsetDateTime.now()
    )
}