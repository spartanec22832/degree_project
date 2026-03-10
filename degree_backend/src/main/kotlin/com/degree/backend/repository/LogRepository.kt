package com.degree.backend.repository

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import com.degree.backend.model.entity.Log

@Repository
interface LogRepository : JpaRepository<Log, Long> {

    @Modifying
    @Transactional
    @Query(
        value = """
            insert into logs (user_id, action_type, metadata)
            values (:userId, :actionType, cast(:metadata as jsonb))
        """,
        nativeQuery = true
    )
    fun insertLog(
        @Param("userId") userId: Long?,
        @Param("actionType") actionType: Int,
        @Param("metadata") metadata: String?
    )
}