package com.degree.backend.exception

import com.degree.backend.model.dto.ApiError
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.http.converter.HttpMessageNotReadableException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ApiException::class)
    fun handleApi(ex: ApiException, req: HttpServletRequest): ResponseEntity<ApiError> {
        return ResponseEntity
            .status(ex.status)
            .body(ApiError(code = ex.code, message = ex.message, details = ex.details, path = req.requestURI))
    }

    // Валидация @Valid
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, req: HttpServletRequest): ResponseEntity<ApiError> {
        val details = ex.bindingResult.allErrors
            .filterIsInstance<FieldError>()
            .associate { it.field to (it.defaultMessage ?: "Некорректное значение") }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiError(code = "VALIDATION_ERROR", message = "Некорректные данные", details = details, path = req.requestURI))
    }

    // Невалидный JSON / неправильные типы
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleBadJson(ex: HttpMessageNotReadableException, req: HttpServletRequest): ResponseEntity<ApiError> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiError(code = "BAD_JSON", message = "Некорректный формат запроса", path = req.requestURI))
    }

    // Неверный логин/пароль
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException, req: HttpServletRequest): ResponseEntity<ApiError> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiError(code = "INVALID_CREDENTIALS", message = "Неверный логин или пароль", path = req.requestURI))
    }

    // Другие security-ошибки
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuth(ex: AuthenticationException, req: HttpServletRequest): ResponseEntity<ApiError> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiError(code = "UNAUTHORIZED", message = "Требуется авторизация", path = req.requestURI))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException, req: HttpServletRequest): ResponseEntity<ApiError> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiError(code = "FORBIDDEN", message = "Недостаточно прав", path = req.requestURI))
    }

    // Фолбэк на всё остальное
    @ExceptionHandler(Exception::class)
    fun handleUnknown(ex: Exception, req: HttpServletRequest): ResponseEntity<ApiError> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiError(code = "INTERNAL_ERROR", message = "Внутренняя ошибка сервера", path = req.requestURI))
    }
}