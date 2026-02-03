package com.degree.backend.controller

import com.degree.backend.model.dto.FavoriteRequest
import com.degree.backend.model.dto.PlaceDto
import com.degree.backend.model.dto.RatingRequest
import com.degree.backend.model.entity.User
import com.degree.backend.service.InteractionService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.security.Principal
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.security.SecurityRequirement

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/interaction")
class InteractionController(
    private val interactionService: InteractionService
) {

    // Вспомогательный метод для получения ID из токена
    private fun getUserId(principal: Principal): Long {
        val user = (principal as UsernamePasswordAuthenticationToken).principal as User
        return user.id
    }

    // Поставить/Убрать лайк
    @PostMapping("/favorite")
    fun toggleFavorite(
        @Valid @RequestBody request: FavoriteRequest,
        principal: Principal
    ): ResponseEntity<Void> {
        interactionService.toggleFavorite(getUserId(principal), request)
        return ResponseEntity.ok().build()
    }

    // Получить список избранного
    @GetMapping("/favorite")
    fun getUserFavorites(principal: Principal): ResponseEntity<List<PlaceDto>> {
        return ResponseEntity.ok(interactionService.getUserFavorites(getUserId(principal)))
    }

    // Поставить оценку
    @PostMapping("/rating")
    fun setRating(
        @Valid @RequestBody request: RatingRequest,
        principal: Principal
    ): ResponseEntity<Void> {
        interactionService.setRating(getUserId(principal), request)
        return ResponseEntity.ok().build()
    }
}