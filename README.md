# 🗺️ Документация ПО "Турист Таганрога"

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)


---

## 📋 Содержание

1. [Описание проекта](#-описание-проекта)
2. [Технологический стек](#-технологический-стек)
3. [Архитектура и структура монорепозитория](#-архитектура-и-структура-монорепозитория)
4. [Локальное развертывание и запуск](#-локальное-развертывание-и-запуск)

---

## 📖 Описание проекта

«Турист Таганрога» — это персональный цифровой гид по городу с богатым культурным наследим. 
Проект решает ключевые проблемы туристов: удобный поиск локаций, интерактивная навигация по карте, работа в условиях нестабильного мобильного интернета.

Архитектура системы построена с акцентом на безопасность (JWT с механизмом отзыва сессий), производительность (многоуровневое кеширование на клиенте) и строгое разделение зон ответственности.

---

## 🛠 Технологический стек

### 📱 Клиентская часть (Android Application)
* **Язык разработки:** Kotlin (JVM Target 17)
* **Интерфейс (UI):** Jetpack Compose, Material Design 3
* **Архитектурный паттерн:** MVVM в связке с Clean Architecture (Разделение на Data, Domain и UI слои)
* **Асинхронность:** Kotlin Coroutines & StateFlow
* **Управление зависимостями (DI):** Dagger Hilt
* **Сетевой слой:** Retrofit + OkHttp
* **Локальная база данных:** Room DB
* **Работа с картами:** Yandex MapKit SDK
* **Загрузка и кэш изображений:** Coil
* **Оптимизация билда:** R8/ProGuard

### ⚙️ Серверная часть (REST API Backend)
* **Язык разработки:** Kotlin + сборщик Gradle
* **Фреймворк:** Spring Boot (Spring Web, Spring Data JPA)
* **Безопасность:** Spring Security + Stateless JWT аутентификация
* **База данных:** PostgreSQL 17
* **Управление схемами БД:** Flyway

### 🐳 Инфраструктура и DevOps
* **Контейнеризация:** Docker, Docker Compose
* **Веб-сервер / Reverse Proxy:** Caddy Server 2 (Автоматическое получение и продление SSL-сертификатов Let's Encrypt, проксирование трафика)

---

## 📁 Архитектура и структура монорепозитория

Проект организован в виде единого репозитория, где каждый компонент изолирован в собственной директории:

```text
📦 degree_project/
 ┣ 📂 degree_android/         # Нативное Android-приложение (клиент)
 ┃ ┣ 📂 app/src/main/java/com/sfedu/degree_android/
 ┃ ┃ ┣ 📂 data/               # Репозитории, Room DB, DTO (Retrofit), мапперы
 ┃ ┃ ┣ 📂 domain/             # Бизнес-логика, модели данных
 ┃ ┃ ┗ 📂 ui/                 # Экранные компоненты Compose, ViewModels, темы оформления
 ┃ ┗ 📜 build.gradle.kts      # Конфигурация сборки
 ┣ 📂 degree_backend/         # Исходный код Spring Boot сервера
 ┃ ┣ 📂 src/main/kotlin/      # Контроллеры, Сервисы, Сущности JPA
 ┃ ┗ 📂 src/main/resources/   # Миграции Flyway (db/migration), .yml-файлы
 ┣ 📂 infrastructure/         # Инфраструктурный слой для деплоя
 ┃ ┣ 📜 docker-compose.yml    # Оркестрация контейнеров (Postgres, Backend, Caddy)
 ┃ ┣ 📜 Caddyfile             # Конфигурация проксирования и HTTPS-сертификатов
 ┃ ┗ 📜 .env.example          # Образец заполнения .env
 ┗ 📜 README.md               # Файл документации
```

## 🚀 Локальное развертывание и запуск

### 1. Клонирование репозитория

Склонируйте проект командой:

```bash
git clone https://github.com/spartanec22832/degree_project.git
```

### 2. Запуск Backend-инфраструктуры (Docker)

Перейдите в папку с настройками инфраструктуры:

```bash
cd degree_infrastructure
```

Создайте файл `.env` по образцу `.env.example`:

```
POSTGRES_DB=<YOUR_DB_NAME>
POSTGRES_USER=<YOUR_USERNAME>
POSTGRES_PASSWORD=<YOUR_PASSWORD>
```

### 3. Настройка и запуск Android-клиента

Перейдите в папку `degree_android`

В корне создайте файл `local.properties`:

```
YANDEX_MAPKIT_API_KEY=<ur_secret_api_key>
```
