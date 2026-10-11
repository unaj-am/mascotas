package com.app.core.domain.repository

import com.app.core.domain.model.User

interface UserRepository {

    suspend fun getUserById(
        userId: String
    ): User?

    suspend fun saveUser(
        user: User
    )

    suspend fun updateUser(
        user: User
    )
}