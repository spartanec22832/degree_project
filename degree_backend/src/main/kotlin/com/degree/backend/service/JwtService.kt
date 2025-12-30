package com.degree.backend.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Date
import java.util.function.Function

@Service
class JwtService {

    // ВАЖНО: Ключ должен быть длинным (минимум 256 бит для HS256).
    // В продакшене его хранят в application.properties, но пока оставим здесь.
    private val SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"

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
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 часа
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
        val keyBytes = Decoders.BASE64.decode(SECRET_KEY)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}