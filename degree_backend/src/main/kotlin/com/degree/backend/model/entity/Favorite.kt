package com.degree.backend.model.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.OffsetDateTime

@Embeddable
data class FavoriteId(
    @Column(name = "user_id")
    val userId: Long = 0,

    @Column(name = "place_id")
    val placeId: Int = 0
) : Serializable

@Entity
@Table(name = "favorites")
data class Favorite(
    @EmbeddedId
    val id: FavoriteId = FavoriteId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Связываем с полем userId в ключе
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("placeId") // Связываем с полем placeId в ключе
    @JoinColumn(name = "place_id")
    val place: Place,

    @Column(name = "added_at")
    val addedAt: OffsetDateTime = OffsetDateTime.now()
)