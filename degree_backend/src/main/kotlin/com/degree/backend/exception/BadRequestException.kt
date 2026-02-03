package com.degree.backend.exception

import org.springframework.http.HttpStatus

class BadRequestException(code: String, message: String, details: Map<String, Any?>? = null) :
    ApiException(code, message, HttpStatus.BAD_REQUEST, details)