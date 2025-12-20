package com.degree.backend.repository

import com.degree.backend.model.entity.Log
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LogRepository : JpaRepository<Log, Long>