package com.degree.backend.service

import com.degree.backend.model.dto.AuthenticationRequest
import com.degree.backend.model.dto.AuthenticationResponse
import com.degree.backend.model.dto.ChangePasswordRequest
import com.degree.backend.model.dto.RegisterRequest
import com.degree.backend.model.entity.Token
import com.degree.backend.model.entity.User
import com.degree.backend.model.mapper.toEntity
import com.degree.backend.repository.TokenRepository
import com.degree.backend.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Principal
import java.time.OffsetDateTime
import com.degree.backend.exception.BadRequestException
import com.degree.backend.exception.ConflictException
import com.degree.backend.exception.NotFoundException
import com.degree.backend.model.entity.ActionType

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val auditLogService: AuditLogService
) {

    @Transactional
    fun register(request: RegisterRequest): AuthenticationResponse {
        if (userRepository.existsByLogin(request.username)) {
            throw ConflictException(
                code = "USERNAME_ALREADY_EXISTS",
                message = "Пользователь с таким логином уже существует"
            )
        }

        val encryptedPassword = passwordEncoder.encode(request.password)
        val user = request.toEntity(encryptedPassword)
        val savedUser = userRepository.save(user)

        val jwtToken = jwtService.generateToken(savedUser)
        saveUserToken(savedUser, jwtToken)

        auditLogService.log(
            actionType = ActionType.USER_REGISTERED,
            user = savedUser,
            metadata = mapOf(
                "username" to savedUser.login
            )
        )

        return AuthenticationResponse(accessToken = jwtToken)
    }

    @Transactional
    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        val user = userRepository.findByLogin(request.username)
            .orElseThrow {
                NotFoundException(
                    code = "USER_NOT_FOUND",
                    message = "Пользователь не найден"
                )
            }

        val jwtToken = jwtService.generateToken(user)
        saveUserToken(user, jwtToken)

        auditLogService.log(
            actionType = ActionType.USER_AUTHENTICATED,
            user = user,
            metadata = mapOf(
                "username" to user.login
            )
        )

        return AuthenticationResponse(accessToken = jwtToken)
    }

    @Transactional
    fun changePassword(request: ChangePasswordRequest, connectedUser: Principal) {
        val user = userRepository.findByLogin(connectedUser.name)
            .orElseThrow { NotFoundException("USER_NOT_FOUND", "Пользователь не найден") }

        if (!passwordEncoder.matches(request.currentPassword, user.passwordEncrypted)) {
            throw BadRequestException("CURRENT_PASSWORD_INVALID", "Неверный текущий пароль")
        }

        if (request.newPassword != request.confirmationPassword) {
            throw BadRequestException("PASSWORDS_DO_NOT_MATCH", "Пароли не совпадают")
        }

        user.passwordEncrypted = passwordEncoder.encode(request.newPassword)
        userRepository.save(user)
        revokeAllUserTokens(user)

        auditLogService.log(
            actionType = ActionType.PASSWORD_CHANGED,
            user = user,
            metadata = mapOf(
                "username" to user.login
            )
        )
    }

    @Transactional
    fun logout(authHeader: String?) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return
        }

        val jwt = authHeader.substring(7)
        val storedToken = tokenRepository.findByToken(jwt).orElse(null) ?: return

        if (!storedToken.revoked) {
            storedToken.revoked = true
            tokenRepository.save(storedToken)
        }

        auditLogService.log(
            actionType = ActionType.USER_LOGOUT,
            user = storedToken.user,
            metadata = mapOf(
                "tokenId" to storedToken.id
            )
        )
    }

    @Transactional
    fun logoutAll(connectedUser: Principal) {
        val user = userRepository.findByLogin(connectedUser.name)
            .orElseThrow { NotFoundException("USER_NOT_FOUND", "Пользователь не найден") }

        revokeAllUserTokens(user)

        auditLogService.log(
            actionType = ActionType.USER_LOGOUT_ALL,
            user = user,
            metadata = mapOf(
                "username" to user.login
            )
        )
    }

    // --- Приватные методы ---

    private fun saveUserToken(user: User, jwtToken: String) {
        val token = Token(
            user = user,
            token = jwtToken,
            revoked = false,
            expiresAt = OffsetDateTime.now().plusDays(30)
        )
        tokenRepository.save(token)
    }

    private fun revokeAllUserTokens(user: User) {
        val validUserTokens = tokenRepository.findAllValidTokenByUser(user.id)
        if (validUserTokens.isEmpty()) return

        validUserTokens.forEach { token ->
            token.revoked = true
        }
        tokenRepository.saveAll(validUserTokens)
    }
}