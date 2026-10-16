
package com.app.core.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun signIn(email: String, password: String): String {
        val result = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()

        return result.user?.uid
            ?: throw IllegalStateException("No se pudo obtener el usuario autenticado")
    }

    suspend fun signUp(email: String, password: String): String {
        val result = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()

        return result.user?.uid
            ?: throw IllegalStateException("No se pudo obtener el usuario registrado")
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}
