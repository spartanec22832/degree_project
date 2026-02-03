package com.degree.backend.exception

import org.springframework.http.HttpStatus

class ConflictException(code: String, message: String, details: Map<String, Any?>? = null) :
    ApiException(code, message, HttpStatus.CONFLICT, details)