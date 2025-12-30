package com.degree.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
    private val jwtAuthFilter: JwtAuthenticationFilter,
    private val authenticationProvider: AuthenticationProvider
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // 1. Отключаем CSRF, так как мы используем REST API и JWT (сессии нет)
            .csrf { it.disable() }

            // 2. Настраиваем права доступа к URL
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/auth/**",  // Открываем доступ к регистрации и логину
                    "/v2/api-docs",     // Разрешаем документацию Swagger (если будешь подключать)
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-resources",
                    "/swagger-resources/**",
                    "/configuration/ui",
                    "/configuration/security",
                    "/swagger-ui/**",
                    "/webjars/**",
                    "/swagger-ui.html"
                ).permitAll()

                // Все остальные запросы требуют аутентификации
                it.anyRequest().authenticated()
            }

            // 3. Указываем, что сессия STATELESS (сервер не запоминает клиента, каждый запрос с токеном)
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            // 4. Подключаем наш AuthenticationProvider (с базой данных и энкодером паролей)
            .authenticationProvider(authenticationProvider)

            // 5. Вставляем наш фильтр ПЕРЕД стандартным фильтром логина
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}