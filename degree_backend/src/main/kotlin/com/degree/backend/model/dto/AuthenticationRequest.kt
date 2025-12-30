package com.degree.backend.model.dto

data class AuthenticationRequest(
    val username: String,
    val password: String
)