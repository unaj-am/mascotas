package com.app.core.data.notifications

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun saveToken(
        userId: String,
        token: String
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("fcmTokens")
            .document(token)
            .set(
                mapOf(
                    "token" to token,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )
            .await()
    }
    suspend fun deleteToken(
        userId: String,
        token: String
    ) {
        firestore.collection("users")
            .document(userId)
            .collection("fcmTokens")
            .document(token)
            .delete()
            .await()
    }

}