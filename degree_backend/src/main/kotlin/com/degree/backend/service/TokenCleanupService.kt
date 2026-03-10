package com.degree.backend.service

import com.degree.backend.repository.TokenRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class TokenCleanupService(
    private val tokenRepository: TokenRepository
) {

    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupTokens() {
        tokenRepository.deleteAllRevokedTokens()
        tokenRepository.deleteAllExpiredTokens(OffsetDateTime.now())
    }
}