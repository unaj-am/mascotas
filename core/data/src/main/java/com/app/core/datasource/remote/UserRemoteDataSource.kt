package com.app.core.data.datasource.remote

import com.app.core.domain.model.User
import com.app.core.domain.model.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val usersCollection
        get() = firestore.collection("users")

    suspend fun getUserById(userId: String): User? {
        val document = usersCollection
            .document(userId)
            .get()
            .await()

        if (!document.exists()) return null

        val roleName = document.getString("role") ?: return null
        val role = runCatching {
            UserRole.valueOf(roleName)
        }.getOrNull() ?: return null

        return User(
            id = document.id,
            name = document.getString("name") ?: "",
            lastName = document.getString("lastName") ?: "",
            email = document.getString("email") ?: "",
            phone = document.getString("phone") ?: "",
            profileImage = document.getString("profileImage"),
            role = role,
            active = document.getBoolean("active") ?: true
        )
    }

    suspend fun saveUser(user: User) {
        val data = mapOf(
            "name" to user.name,
            "lastName" to user.lastName,
            "email" to user.email,
            "phone" to user.phone,
            "profileImage" to user.profileImage,
            "role" to user.role.name,
            "active" to user.active
        )

        usersCollection
            .document(user.id)
            .set(data)
            .await()
    }

    suspend fun updateUser(user: User) {
        saveUser(user)
    }
}