package com.degree.backend.config

import com.degree.backend.model.dto.ApiError
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.ServletWebRequest

@Controller
class ApiErrorController(
    private val errorAttributes: ErrorAttributes
) : ErrorController {

    @RequestMapping("/error")
    fun error(request: HttpServletRequest): ResponseEntity<ApiError> {
        val webRequest = ServletWebRequest(request)

        val attrs = errorAttributes.getErrorAttributes(
            webRequest,
            ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE)
        )

        val status = (attrs["status"] as? Int) ?: 500
        val path = (attrs["path"] as? String) ?: request.requestURI
        val message = (attrs["message"] as? String)?.takeIf { it.isNotBlank() }
            ?: defaultMessage(status)

        val code = when (status) {
            400 -> "BAD_REQUEST"
            401 -> "UNAUTHORIZED"
            403 -> "FORBIDDEN"
            404 -> "NOT_FOUND"
            405 -> "METHOD_NOT_ALLOWED"
            else -> "HTTP_$status"
        }

        return ResponseEntity.status(status).body(
            ApiError(code = code, message = message, path = path)
        )
    }

    private fun defaultMessage(status: Int) = when (status) {
        404 -> "Ресурс не найден"
        405 -> "Метод не поддерживается"
        400 -> "Некорректный запрос"
        else -> "Внутренняя ошибка сервера"
    }
}