package com.app.core.data.repository

import com.app.core.data.datasource.WalkerRemoteDataSource
import com.app.core.database.dao.WalkerDao
import com.app.core.database.mapper.toDomain
import com.app.core.database.mapper.toEntity
import com.app.core.domain.model.Walker
import com.app.core.domain.repository.WalkerRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkerRepositoryImpl @Inject constructor(
    private val walkerRemoteDataSource: WalkerRemoteDataSource,
    private val firebaseAuth: FirebaseAuth,
    private val walkerDao: WalkerDao
) : WalkerRepository {

    private fun requireCurrentUserId(): String {
        return firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debés iniciar sesión para gestionar el perfil de paseador"
            )
    }

    override suspend fun getWalkers(): List<Walker> {

        return try {

            val walkers = walkerRemoteDataSource.getWalkers()

            walkerDao.replaceAllWalkers(
                walkers.map { it.toEntity() }
            )

            walkers

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: FirebaseFirestoreException) {

            if (!exception.isConnectivityError()) {
                throw exception
            }

            getLocalWalkers()

        } catch (exception: FirebaseNetworkException) {

            getLocalWalkers()
        }
    }

    override suspend fun getWalkerById(
        userId: String
    ): Walker? {

        requireCurrentUserId()

        require(userId.isNotBlank()) {
            "El identificador del paseador no puede estar vacío"
        }

        return try {

            val walker = walkerRemoteDataSource
                .getWalkerById(userId)

            if (walker == null) {
                walkerDao.deleteWalkerById(userId)
            } else {
                walkerDao.insertWalker(walker.toEntity())
            }

            walker

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: FirebaseFirestoreException) {

            if (!exception.isConnectivityError()) {
                throw exception
            }

            getLocalWalker(userId)

        } catch (exception: FirebaseNetworkException) {

            getLocalWalker(userId)
        }
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

        walkerDao.insertWalker(walker.toEntity())
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

        walkerDao.insertWalker(walker.toEntity())
    }

    override suspend fun getAvailableWalkers(): List<Walker> {

        return try {

            val walkers = walkerRemoteDataSource
                .getAvailableWalkers()

            walkerDao.replaceAvailableWalkers(
                walkers.map { it.toEntity() }
            )

            walkers

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: FirebaseFirestoreException) {

            if (!exception.isConnectivityError()) {
                throw exception
            }

            getLocalAvailableWalkers()

        } catch (exception: FirebaseNetworkException) {

            getLocalAvailableWalkers()
        }
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

        walkerDao.insertWalker(
            existingWalker.copy(
                available = available
            ).toEntity()
        )
    }

    private suspend fun getLocalWalkers(): List<Walker> {
        return walkerDao
            .getAllWalkers()
            .first()
            .map { it.toDomain() }
    }

    private suspend fun getLocalWalker(
        userId: String
    ): Walker? {
        return walkerDao
            .getWalkerById(userId)
            ?.toDomain()
    }

    private suspend fun getLocalAvailableWalkers(): List<Walker> {
        return walkerDao
            .getAvailableWalkers()
            .first()
            .map { it.toDomain() }
    }

    private fun FirebaseFirestoreException.isConnectivityError(): Boolean {
        return code == FirebaseFirestoreException.Code.UNAVAILABLE ||
                code == FirebaseFirestoreException.Code.DEADLINE_EXCEEDED
    }
}
