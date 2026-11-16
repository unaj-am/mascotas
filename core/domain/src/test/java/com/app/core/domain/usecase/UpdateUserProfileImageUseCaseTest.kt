package com.app.core.domain.usecase

import com.app.core.domain.model.User
import com.app.core.domain.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UpdateUserProfileImageUseCaseTest {

    @Test
    fun invoke_updatesTheRequestedUserImage() = runBlocking {
        val repository = RecordingUserRepository()
        val useCase = UpdateUserProfileImageUseCase(repository)

        useCase("user-1", "https://example.com/profile.jpg")

        assertEquals("user-1" to "https://example.com/profile.jpg", repository.updatedImage)
    }

    @Test
    fun invoke_rejectsBlankImageUrl() = runBlocking {
        val repository = RecordingUserRepository()
        val useCase = UpdateUserProfileImageUseCase(repository)

        val error = runCatching { useCase("user-1", " ") }.exceptionOrNull()

        assertEquals("La URL de la imagen es obligatoria", error?.message)
        assertNull(repository.updatedImage)
    }

    private class RecordingUserRepository : UserRepository {
        var updatedImage: Pair<String, String>? = null

        override suspend fun getUserById(userId: String): User? = null
        override suspend fun saveUser(user: User) = Unit
        override suspend fun updateUser(user: User) = Unit
        override suspend fun updateProfileImage(userId: String, imageUrl: String) {
            updatedImage = userId to imageUrl
        }
    }
}
