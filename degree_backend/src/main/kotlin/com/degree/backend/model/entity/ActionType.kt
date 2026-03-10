package com.degree.backend.model.entity

enum class ActionType(val code: Int) {
    USER_REGISTERED(1),
    USER_AUTHENTICATED(2),
    USER_LOGOUT(3),
    USER_LOGOUT_ALL(4),
    PASSWORD_CHANGED(5),

    FAVORITE_ADDED(10),
    FAVORITE_REMOVED(11),

    RATING_CREATED(20),
    RATING_UPDATED(21)
}