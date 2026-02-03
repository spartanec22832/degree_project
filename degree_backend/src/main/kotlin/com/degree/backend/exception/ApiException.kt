package com.degree.backend.exception

import org.springframework.http.HttpStatus

open class ApiException(
    val code: String,
    override val message: String,
    val status: HttpStatus,
    val details: Map<String, Any?>? = null
) : RuntimeException(message)
