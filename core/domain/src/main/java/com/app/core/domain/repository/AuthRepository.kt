
package com.app.core.domain.repository

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
}
