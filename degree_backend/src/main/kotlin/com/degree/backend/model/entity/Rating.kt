package com.degree.backend.model.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.OffsetDateTime

@Embeddable
data class RatingId(
    @Column(name = "user_id")
    val userId: Long = 0,

    @Column(name = "place_id")
    val placeId: Int = 0
) : Serializable

@Entity
@Table(name = "ratings")
data class Rating(
    @EmbeddedId
    val id: RatingId = RatingId(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("placeId")
    @JoinColumn(name = "place_id")
    val place: Place,

    @Column(nullable = false)
    val rating: Short,

    @Column(name = "updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)