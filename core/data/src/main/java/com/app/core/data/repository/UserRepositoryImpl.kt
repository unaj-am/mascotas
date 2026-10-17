package com.app.core.data.repository

import com.app.core.data.datasource.remote.UserRemoteDataSource
import com.app.core.domain.model.User
import com.app.core.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun getUserById(userId: String): User? {
        return userRemoteDataSource.getUserById(userId)
    }

    override suspend fun saveUser(user: User) {
        userRemoteDataSource.saveUser(user)
    }

    override suspend fun updateUser(user: User) {
        userRemoteDataSource.updateUser(user)
    }
}