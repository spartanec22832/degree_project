package com.degree.backend.controller

import com.degree.backend.model.dto.AuthenticationRequest
import com.degree.backend.model.dto.AuthenticationResponse
import com.degree.backend.model.dto.ChangePasswordRequest
import com.degree.backend.model.dto.RegisterRequest
import com.degree.backend.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authService.register(request))
    }

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody request: AuthenticationRequest): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok(authService.authenticate(request))
    }

    @PatchMapping("/change-password")
    fun changePassword(
        @RequestBody request: ChangePasswordRequest,
        principal: Principal
    ): ResponseEntity<Void> {
        authService.changePassword(request, principal)
        return ResponseEntity.ok().build()
    }
}