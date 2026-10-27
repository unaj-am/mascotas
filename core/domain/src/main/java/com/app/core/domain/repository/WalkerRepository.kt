package com.app.core.domain.repository

import com.app.core.domain.model.Walker

interface WalkerRepository {

    suspend fun getWalkers(): List<Walker>

    suspend fun getWalkerById(userId: String): Walker?

    suspend fun saveWalker(walker: Walker)

    suspend fun updateWalker(walker: Walker)

    suspend fun getAvailableWalkers(): List<Walker>

    suspend fun updateAvailability(
        userId: String,
        available: Boolean
    )
}