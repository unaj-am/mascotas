package com.app.core.data.repository

import com.app.core.domain.repository.AuthRepository
import com.app.core.data.remote.firebase.FirebaseAuthDataSource

class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<String> {
        return runCatching {
            dataSource.signIn(email, password)
        }
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<String> {
        return runCatching {
            dataSource.signUp(email, password)
        }
    }

    override suspend fun signOut() {
        dataSource.signOut()
    }

    override fun getCurrentUserId(): String? {
        return dataSource.getCurrentUserId()
    }
}