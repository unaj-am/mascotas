package com.app.core.datasource.remote

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject



class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun signIn(
        email: String,
        password: String
    ): String {
        val result = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()

        return result.user?.uid
            ?: throw IllegalStateException(
                "No se pudo obtener el usuario autenticado"
            )
    }

    suspend fun signUp(
        email: String,
        password: String
    ): String {
        val result = firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()

        return result.user?.uid
            ?: throw IllegalStateException(
                "No se pudo obtener el usuario registrado"
            )
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun observeAuthState(): Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }


    suspend fun sendEmailVerification() {
        val user = firebaseAuth.currentUser
            ?: throw IllegalStateException(
                "No hay un usuario autenticado"
            )

        user.sendEmailVerification().await()
    }

    suspend fun isEmailVerified(): Boolean {
        val user = firebaseAuth.currentUser
            ?: return false

        user.reload().await()

        return firebaseAuth.currentUser?.isEmailVerified == true
    }

    suspend fun resendEmailVerification() {
        val user = firebaseAuth.currentUser
            ?: throw IllegalStateException(
                "No hay un usuario autenticado"
            )

        user.reload().await()

        val updatedUser = firebaseAuth.currentUser
            ?: throw IllegalStateException(
                "No hay un usuario autenticado"
            )

        if (updatedUser.isEmailVerified) {
            return
        }

        updatedUser.sendEmailVerification().await()
    }

}