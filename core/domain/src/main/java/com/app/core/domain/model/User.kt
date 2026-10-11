package com.app.core.domain.model

data class User(
    val id: String,
    val name: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val profileImage: String? = null,
    val role: UserRole,
    val active: Boolean = true
)