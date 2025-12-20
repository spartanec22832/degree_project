package com.degree.backend.repository

import com.degree.backend.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): Optional<User>

    fun existsByUsername(username: String): Boolean
}