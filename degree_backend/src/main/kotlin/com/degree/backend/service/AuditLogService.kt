package com.degree.backend.service

import com.degree.backend.model.entity.ActionType
import com.degree.backend.model.entity.User
import com.degree.backend.repository.LogRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuditLogService(
    private val logRepository: LogRepository,
    private val objectMapper: ObjectMapper
) {

    @Transactional
    fun log(
        actionType: ActionType,
        user: User? = null,
        metadata: Map<String, Any?> = emptyMap()
    ) {
        val metadataJson = if (metadata.isEmpty()) "{}" else objectMapper.writeValueAsString(metadata)

        logRepository.insertLog(
            userId = user?.id,
            actionType = actionType.code,
            metadata = metadataJson
        )
    }
}