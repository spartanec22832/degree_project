package com.degree.backend.controller

import com.degree.backend.model.dto.UserDto
import com.degree.backend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import io.swagger.v3.oas.annotations.security.SecurityRequirement

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/profile")
    fun getProfile(principal: Principal): ResponseEntity<UserDto> {
        // Principal.name в нашем случае — это username (login)
        return ResponseEntity.ok(userService.getUserProfile(principal.name))
    }
}