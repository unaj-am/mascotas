
package com.app.core.data.repository

import com.app.core.data.datasource.FirebaseAuthDataSource
import com.app.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
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

    override fun observeAuthState(): Flow<String?> {
        return dataSource.observeAuthState()
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return runCatching {
            dataSource.sendEmailVerification()
        }
    }

    override suspend fun isEmailVerified(): Result<Boolean> {
        return runCatching {
            dataSource.isEmailVerified()
        }
    }

    override suspend fun resendEmailVerification(): Result<Unit> {
        return runCatching {
            dataSource.resendEmailVerification()
        }
    }
}
