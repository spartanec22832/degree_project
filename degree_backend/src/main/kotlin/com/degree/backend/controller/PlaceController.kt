package com.degree.backend.controller

import com.degree.backend.model.dto.PlaceDto
import com.degree.backend.model.dto.PlaceMapDto
import com.degree.backend.model.entity.User
import com.degree.backend.service.PlaceService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/places")
class PlaceController(
    private val placeService: PlaceService
) {

    // 1. Точки для карты (MapDto) - доступно всем
    @GetMapping("/map")
    fun getAllPlacesForMap(): ResponseEntity<List<PlaceMapDto>> {
        return ResponseEntity.ok(placeService.getAllPlacesForMap())
    }

    // 2. Поиск - доступно всем
    @GetMapping("/search")
    fun searchPlaces(@RequestParam query: String): ResponseEntity<List<PlaceMapDto>> {
        return ResponseEntity.ok(placeService.searchPlaces(query))
    }

    // 3. Детали места (PlaceDto)
    @GetMapping("/{id}")
    fun getPlaceDetails(
        @PathVariable id: Int,
        principal: Principal? // Может быть null, если юзер не авторизован
    ): ResponseEntity<PlaceDto> {

        // Пытаемся достать ID юзера, если он есть
        val userId = if (principal != null) {
            // Хитрость: приводим Principal к нашему Entity User, чтобы взять ID
            val user = (principal as UsernamePasswordAuthenticationToken).principal as User
            user.id
        } else {
            null
        }

        return ResponseEntity.ok(placeService.getPlaceDetails(id, userId))
    }
}