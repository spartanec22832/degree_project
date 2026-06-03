package com.degree.backend.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Date
import java.util.function.Function
import java.util.UUID

@Service
class JwtService(
    @Value("\${jwt.secret}") private val secretKey: String
) {

    private val JWT_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 30 // 30 дней

    // 1. Извлечь имя пользователя (логин) из токена
    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    // 2. Универсальный метод извлечения конкретного данного (Claim)
    fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    // 3. Генерация токена (без дополнительных полей)
    fun generateToken(userDetails: UserDetails): String {
        return generateToken(HashMap(), userDetails)
    }

    // 4. Генерация токена (с дополнительными полями, если нужно)
    fun generateToken(extraClaims: Map<String, Any>, userDetails: UserDetails): String {
        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.username) // Сюда попадет то, что возвращает getUsername()
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
            .setId(UUID.randomUUID().toString())
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    // 5. Проверка валидности токена
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        // Токен валиден, если имя совпадает и срок не истек
        return (username == userDetails.username) && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    // Парсинг токена (расшифровка)
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    // Декодирование секретного ключа
    private fun getSignInKey(): Key {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}