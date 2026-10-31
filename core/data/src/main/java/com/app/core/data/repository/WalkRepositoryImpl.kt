
package com.app.core.data.repository

import com.app.core.data.remote.walk.WalkRemoteDataSource
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkRepository
import com.google.firebase.auth.FirebaseAuth
import java.time.Instant
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

    override suspend fun updateWalkStatus(
        walkId: String,
        status: WalkStatus
    ) {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val currentUserId = requireCurrentUserId()

        walkRemoteDataSource.updateWalkStatus(
            walkId = walkId
        ) { walk ->

            require(
                walk.ownerId == currentUserId ||
                        walk.walkerId == currentUserId
            ) {
                "No tenés permiso para modificar este paseo"
            }

            when (status) {
                WalkStatus.IN_PROGRESS -> {
                    require(walk.walkerId == currentUserId) {
                        "Solo el paseador puede iniciar el paseo"
                    }
                    require(walk.status == WalkStatus.SCHEDULED) {
                        "Solo se pueden iniciar paseos programados"
                    }
                }

                WalkStatus.COMPLETED -> {
                    require(walk.walkerId == currentUserId) {
                        "Solo el paseador puede finalizar el paseo"
                    }
                    require(walk.status == WalkStatus.IN_PROGRESS) {
                        "Solo se pueden finalizar paseos en curso"
                    }
                }

                WalkStatus.CANCELLED -> {
                    require(walk.ownerId == currentUserId) {
                        "Solo el dueño puede cancelar este paseo"
                    }
                    require(walk.status == WalkStatus.SCHEDULED) {
                        "Solo se pueden cancelar paseos programados"
                    }
                }

                WalkStatus.SCHEDULED -> {
                    throw IllegalArgumentException(
                        "No se puede volver al estado programado"
                    )
                }
            }

            val updates = mutableMapOf<String, Any>(
                "status" to status.name
            )

            when (status) {
                WalkStatus.IN_PROGRESS -> {
                    updates["startedAt"] = Instant.now().toString()
                }

                WalkStatus.COMPLETED -> {
                    updates["finishedAt"] = Instant.now().toString()
                }

                WalkStatus.CANCELLED,
                WalkStatus.SCHEDULED -> Unit
            }

            updates
        }
    }
}
