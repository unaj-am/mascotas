
package com.app.core.data.repository

import com.app.core.data.remote.walker.WalkerRemoteDataSource
import com.app.core.domain.model.Walker
import com.app.core.domain.repository.WalkerRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkerRepositoryImpl @Inject constructor(
    private val walkerRemoteDataSource: WalkerRemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) : WalkerRepository {

    private fun requireCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debés iniciar sesión para gestionar el perfil de paseador"
            )
    }

    override suspend fun getWalkers(): List<Walker> {
        return walkerRemoteDataSource.getWalkers()
    }

    override suspend fun getWalkerById(
        userId: String
    ): Walker? {
        requireCurrentUserId()

        require(userId.isNotBlank()) {
            "El identificador del paseador no puede estar vacío"
        }

        return walkerRemoteDataSource.getWalkerById(userId)
    }

    override suspend fun saveWalker(
        walker: Walker
    ) {
        val currentUserId = requireCurrentUserId()

        require(walker.userId == currentUserId) {
            "No podés crear el perfil de otro paseador"
        }

        val existingWalker = walkerRemoteDataSource
            .getWalkerById(currentUserId)

        require(existingWalker == null) {
            "Ya existe un perfil de paseador para este usuario"
        }

        walkerRemoteDataSource.saveWalker(walker)
    }

    override suspend fun updateWalker(
        walker: Walker
    ) {
        val currentUserId = requireCurrentUserId()

        require(walker.userId == currentUserId) {
            "No podés modificar el perfil de otro paseador"
        }

        val existingWalker = walkerRemoteDataSource
            .getWalkerById(currentUserId)

        require(existingWalker != null) {
            "No existe el perfil de paseador que querés modificar"
        }

        walkerRemoteDataSource.updateWalker(walker)
    }

    override suspend fun getAvailableWalkers(): List<Walker> {
        return walkerRemoteDataSource.getAvailableWalkers()
    }

    override suspend fun updateAvailability(
        userId: String,
        available: Boolean
    ) {
        val currentUserId = requireCurrentUserId()

        require(userId == currentUserId) {
            "No podés modificar la disponibilidad de otro paseador"
        }

        val existingWalker = walkerRemoteDataSource
            .getWalkerById(currentUserId)

        require(existingWalker != null) {
            "No existe el perfil profesional del paseador"
        }

        walkerRemoteDataSource.updateAvailability(
            userId = currentUserId,
            available = available
        )
    }
}
