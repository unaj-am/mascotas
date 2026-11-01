
package com.app.core.data.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fcmTokenDataSource: FcmTokenDataSource
) {

    suspend fun registerCurrentToken() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        val token = FirebaseMessaging
            .getInstance()
            .token
            .await()

        fcmTokenDataSource.saveToken(
            userId = userId,
            token = token
        )
    }
    suspend fun unregisterCurrentToken() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        val token = FirebaseMessaging
            .getInstance()
            .token
            .await()

        fcmTokenDataSource.deleteToken(
            userId = userId,
            token = token
        )
    }
}
