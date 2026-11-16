package com.app.core.domain.usecase

import com.app.core.domain.model.User
import com.app.core.domain.model.UserRole
import com.app.core.domain.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SaveUserUseCaseTest {

    @Test
    fun invoke_persistsValidUser() = runBlocking {
        val repository = RecordingUserRepository()
        val user = User(
            id = "user-1",
            name = "Ada",
            lastName = "Lovelace",
            email = "ada@example.com",
            phone = "12345678",
            role = UserRole.OWNER
        )

        SaveUserUseCase(repository)(user)

        assertEquals(user, repository.savedUser)
    }

    @Test
    fun invoke_rejectsMissingUserNameBeforePersistence() = runBlocking {
        val repository = RecordingUserRepository()
        val user = User(
            id = "user-1",
            name = " ",
            lastName = "Lovelace",
            email = "ada@example.com",
            phone = "12345678",
            role = UserRole.OWNER
        )

        val error = runCatching { SaveUserUseCase(repository)(user) }.exceptionOrNull()

        assertEquals("El nombre del usuario es obligatorio", error?.message)
        assertNull(repository.savedUser)
    }

    private class RecordingUserRepository : UserRepository {
        var savedUser: User? = null
        override suspend fun getUserById(userId: String): User? = null
        override suspend fun saveUser(user: User) {
            savedUser = user
        }
        override suspend fun updateUser(user: User) = Unit
        override suspend fun updateProfileImage(userId: String, imageUrl: String) = Unit
    }
}
