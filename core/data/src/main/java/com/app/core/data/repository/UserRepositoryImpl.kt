package com.app.core.data.repository

import com.app.core.data.datasource.UserRemoteDataSource
import com.app.core.database.dao.UserDao
import com.app.core.database.mapper.toDomain
import com.app.core.database.mapper.toEntity
import com.app.core.domain.model.User
import com.app.core.domain.repository.UserRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUserById(
        userId: String
    ): User? {

        require(userId.isNotBlank()) {
            "El identificador del usuario no puede estar vacío"
        }

        return try {

            val user = userRemoteDataSource.getUserById(userId)

            if (user == null) {
                userDao.deleteUserById(userId)
            } else {
                userDao.insertUser(user.toEntity())
            }

            user

        } catch (exception: CancellationException) {
            throw exception

        } catch (exception: FirebaseFirestoreException) {

            if (!exception.isConnectivityError()) {
                throw exception
            }

            getLocalUser(userId)

        } catch (exception: FirebaseNetworkException) {

            getLocalUser(userId)
        }
    }

    override suspend fun saveUser(user: User) {

        userRemoteDataSource.saveUser(user)

        userDao.insertUser(user.toEntity())
    }

    override suspend fun updateUser(user: User) {

        userRemoteDataSource.updateUser(user)

        userDao.insertUser(user.toEntity())
    }

    override suspend fun updateProfileImage(
        userId: String,
        imageUrl: String
    ) {

        userRemoteDataSource.updateProfileImage(
            userId = userId,
            imageUrl = imageUrl
        )

        userDao.updateProfileImage(
            userId = userId,
            imageUrl = imageUrl
        )
    }

    private suspend fun getLocalUser(
        userId: String
    ): User? {

        return userDao
            .observeUserById(userId)
            .first()
            ?.toDomain()
    }

    private fun FirebaseFirestoreException.isConnectivityError(): Boolean {
        return code == FirebaseFirestoreException.Code.UNAVAILABLE ||
                code == FirebaseFirestoreException.Code.DEADLINE_EXCEEDED
    }
}
