
package com.app.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signIn(
        email: String,
        password: String
    ): Result<String>

    suspend fun signUp(
        email: String,
        password: String
    ): Result<String>

    suspend fun signOut()

    fun getCurrentUserId(): String?

    fun observeAuthState(): Flow<String?>

    suspend fun sendEmailVerification(): Result<Unit>

    suspend fun isEmailVerified(): Result<Boolean>

    suspend fun resendEmailVerification(): Result<Unit>
}
