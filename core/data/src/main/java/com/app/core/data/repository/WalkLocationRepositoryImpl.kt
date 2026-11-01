package com.app.core.data.repository

import com.app.core.data.datasource.WalkLocationRemoteDataSource
import com.app.core.data.remote.walk.WalkRemoteDataSource
import com.app.core.domain.model.WalkLocation
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkLocationRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

@Singleton
class WalkLocationRepositoryImpl @Inject constructor(
    private val walkLocationRemoteDataSource: WalkLocationRemoteDataSource,
    private val walkRemoteDataSource: WalkRemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) : WalkLocationRepository {

    private fun requireCurrentUserId(): String =
        firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debés iniciar sesión para acceder a la ubicación del paseo"
            )

    private suspend fun requireWalkParticipant(walkId: String) =
        walkRemoteDataSource.getWalkById(walkId)
            ?.also { walk ->
                val currentUserId = requireCurrentUserId()

                require(
                    walk.ownerId == currentUserId ||
                            walk.walkerId == currentUserId
                ) {
                    "No tenés permiso para acceder a la ubicación de este paseo"
                }
            }
            ?: throw IllegalStateException(
                "No existe el paseo solicitado"
            )

    override suspend fun publishLocation(
        walkId: String,
        location: WalkLocation
    ) {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        val walk = requireWalkParticipant(walkId)
        val currentUserId = requireCurrentUserId()

        require(walk.walkerId == currentUserId) {
            "Solo el paseador asignado puede publicar ubicaciones"
        }

        require(walk.status == WalkStatus.IN_PROGRESS) {
            "Solo se puede publicar la ubicación durante un paseo en curso"
        }

        walkLocationRemoteDataSource.publishLocation(
            walkId = walkId,
            location = location
        )
    }

    override suspend fun getLastLocation(
        walkId: String
    ): WalkLocation? {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        requireWalkParticipant(walkId)

        return walkLocationRemoteDataSource.getLastLocation(walkId)
    }

    override fun observeCurrentLocation(
        walkId: String
    ): Flow<WalkLocation?> = flow {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        requireWalkParticipant(walkId)

        emitAll(
            walkLocationRemoteDataSource.observeCurrentLocation(walkId)
        )
    }

    override fun observeLocationHistory(
        walkId: String
    ): Flow<List<WalkLocation>> = flow {
        require(walkId.isNotBlank()) {
            "El identificador del paseo es obligatorio"
        }

        requireWalkParticipant(walkId)

        emitAll(
            walkLocationRemoteDataSource.observeLocationHistory(walkId)
        )
    }
}