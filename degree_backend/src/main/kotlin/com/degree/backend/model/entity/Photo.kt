package com.degree.backend.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "photos")
data class Photo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    val place: Place,

    @Column(nullable = false)
    val url: String,

    @Column(name = "is_main")
    val isMain: Boolean = false,

    @Column(name = "order_index")
    val orderIndex: Int = 0
)