package com.degree.backend.controller

import com.degree.backend.model.dto.AuthenticationRequest
import com.degree.backend.model.dto.AuthenticationResponse
import com.degree.backend.model.dto.ChangePasswordRequest
import com.degree.backend.model.dto.RegisterRequest
import com.degree.backend.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import jakarta.validation.Valid
import io.swagger.v3.oas.annotations.security.SecurityRequirement

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authService.register(request))
    }

    @PostMapping("/authenticate")
    fun authenticate(@Valid @RequestBody request: AuthenticationRequest): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authService.authenticate(request))
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/change-password")
    fun changePassword(
        @Valid @RequestBody request: ChangePasswordRequest,
        principal: Principal
    ): ResponseEntity<Void> {
        authService.changePassword(request, principal)
        return ResponseEntity.ok().build()
    }
}