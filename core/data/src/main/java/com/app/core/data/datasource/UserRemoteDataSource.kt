
package com.app.core.data.datasource

import com.app.core.data.mapper.toFirestoreMap
import com.app.core.data.mapper.toUser
import com.app.core.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
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
            .get(Source.SERVER)
            .await()

        if (!document.exists()) return null

        return document.toUser()
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

}
