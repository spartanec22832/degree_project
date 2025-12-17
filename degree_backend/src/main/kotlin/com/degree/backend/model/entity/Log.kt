package com.degree.backend.model.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "logs")
data class Log(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @Column(name = "action_type", nullable = false)
    val actionType: Int,

    @Column(columnDefinition = "jsonb")
    val metadata: String? = null,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)