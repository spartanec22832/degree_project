package com.degree.backend.repository

import com.degree.backend.model.entity.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.Optional

@Repository
interface TokenRepository : JpaRepository<Token, Long> {

    fun findByToken(token: String): Optional<Token>

    @Query("""
        select t from Token t
        where t.token = :token and t.revoked = false and t.expiresAt > CURRENT_TIMESTAMP
    """)
    fun findActiveToken(token: String): Optional<Token>

    @Query("""
        select t from Token t inner join t.user u
        where u.id = :userId and t.revoked = false and t.expiresAt > CURRENT_TIMESTAMP
    """)
    fun findAllValidTokenByUser(userId: Long): List<Token>

    @Modifying
    @Query("delete from Token t where t.revoked = true")
    fun deleteAllRevokedTokens(): Int

    @Modifying
    @Query("delete from Token t where t.expiresAt < :now")
    fun deleteAllExpiredTokens(now: OffsetDateTime): Int
}