package com.degree.backend.exception

import org.springframework.http.HttpStatus

class NotFoundException(code: String, message: String, details: Map<String, Any?>? = null) :
    ApiException(code, message, HttpStatus.NOT_FOUND, details)