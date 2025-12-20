package com.degree.backend.repository

import com.degree.backend.model.entity.Place
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlaceRepository : JpaRepository<Place, Int> {

    fun findByType(type: String): List<Place>

    // поиск места по части названия без учета регистра
    fun findByNameContainingIgnoreCase(name: String): List<Place>
}