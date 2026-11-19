package com.app.core.data.repository

import com.app.core.data.datasource.WalkRemoteDataSource
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkRepositoryImpl @Inject constructor(
    private val walkRemoteDataSource: WalkRemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) : WalkRepository {

    private fun requireCurrentUserId(): String =
        firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debés iniciar sesión para gestionar paseos"
            )

    override suspend fun getWalkById(
        walkId: String
    ): Walk? {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val currentUserId = requireCurrentUserId()

        val walk = walkRemoteDataSource.getWalkById(walkId)
            ?: return null

        require(
            walk.ownerId == currentUserId ||
                    walk.walkerId == currentUserId
        ) {
            "No tenés permiso para consultar este paseo"
        }

        return walk
    }

    override suspend fun getWalksByOwner(
        ownerId: String
    ): List<Walk> {
        require(ownerId == requireCurrentUserId()) {
            "No podés consultar los paseos de otro dueño"
        }

        return walkRemoteDataSource.getWalksByOwner(ownerId)
    }

    override suspend fun getWalksByWalker(
        walkerId: String
    ): List<Walk> {
        require(walkerId == requireCurrentUserId()) {
            "No podés consultar los paseos de otro paseador"
        }

        return walkRemoteDataSource.getWalksByWalker(walkerId)
    }

    override suspend fun saveWalk(
        walk: Walk
    ) {
        require(walk.id.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val currentUserId = requireCurrentUserId()

        require(
            walk.ownerId == currentUserId ||
                    walk.walkerId == currentUserId
        ) {
            "No podés guardar un paseo en el que no participás"
        }

        walkRemoteDataSource.saveWalk(walk)
    }

    override suspend fun startWalk(walkId: String) {
        updateStatus(walkId, WalkStatus.IN_PROGRESS)
    }

    override suspend fun completeWalk(walkId: String) {
        updateStatus(walkId, WalkStatus.COMPLETED)
    }

    override suspend fun cancelWalk(walkId: String) {
        updateStatus(walkId, WalkStatus.CANCELLED)
    }

    private suspend fun updateStatus(walkId: String, status: WalkStatus) {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        walkRemoteDataSource.updateWalkStatus(
            walkId = walkId,
            userId = requireCurrentUserId(),
            status = status
        )
    }
}
