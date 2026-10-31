
package com.app.core.data.datasource

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
        usersCollection
            .document(user.id)
            .set(user.toFirestoreMap())
            .await()
    }

    suspend fun updateUser(user: User) {
        usersCollection
            .document(user.id)
            .update(user.toFirestoreMap())
            .await()
    }

    suspend fun updateProfileImage(
        userId: String,
        imageUrl: String
    ) {
        usersCollection
            .document(userId)
            .update("profileImage", imageUrl)
            .await()
    }

    private fun User.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "lastName" to lastName,
            "email" to email,
            "phone" to phone,
            "profileImage" to profileImage,
            "role" to role.name,
            "active" to active
        )
    }
}
