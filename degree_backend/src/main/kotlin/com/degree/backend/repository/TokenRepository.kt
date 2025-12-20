package com.degree.backend.repository

import com.degree.backend.model.entity.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TokenRepository : JpaRepository<Token, Long> {

    fun findByToken(token: String): Optional<Token>

    @Query("""
        select t from Token t inner join t.user u
        where u.id = :userId and (t.revoked = false)
    """)
    fun findAllValidTokenByUser(userId: Long): List<Token>
}