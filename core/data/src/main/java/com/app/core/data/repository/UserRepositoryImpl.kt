package com.app.core.data.repository

import com.app.core.domain.model.User
import com.app.core.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {

    private val users = mutableListOf<User>()

    override suspend fun getUserById(
        userId: String
    ): User? {
        return users.find { it.id == userId }
    }

    override suspend fun saveUser(
        user: User
    ) {
        users.add(user)
    }

    override suspend fun updateUser(
        user: User
    ) {
        val index = users.indexOfFirst { it.id == user.id }

        if (index >= 0) {
            users[index] = user
        }
    }
}