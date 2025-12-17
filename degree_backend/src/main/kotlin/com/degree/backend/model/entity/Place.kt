package com.degree.backend.model.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "data")
data class Place(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    val name: String? = null,
    val type: String? = null,
    val category: String? = null,
    val description: String? = null,
    val address: String? = null,

    @Column(name = "work_time")
    val workTime: String? = null,

    val price: BigDecimal? = null,

    @Column(name = "contact_link")
    val contactLink: String? = null,

    @Column(name = "contact_phone")
    val contactPhone: String? = null,

    @Column(name = "contact_email")
    val contactEmail: String? = null,

    @Column(name = "cluster_element")
    val clusterElement: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null
)