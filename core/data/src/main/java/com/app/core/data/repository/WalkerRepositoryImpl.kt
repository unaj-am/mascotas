package com.app.core.data.repository

import com.app.core.domain.model.Walker
import com.app.core.domain.repository.WalkerRepository

class WalkerRepositoryImpl : WalkerRepository {

    private val walkers = mutableListOf<Walker>()

    override suspend fun getWalkers(): List<Walker> {
        return walkers.toList()
    }

    override suspend fun getWalkerById(
        userId: String
    ): Walker? {
        return walkers.find { it.userId == userId }
    }

    override suspend fun saveWalker(
        walker: Walker
    ) {
        val exists = walkers.any { it.userId == walker.userId }

        if (!exists) {
            walkers.add(walker)
        }
    }

    override suspend fun updateWalker(
        walker: Walker
    ) {
        val index = walkers.indexOfFirst {
            it.userId == walker.userId
        }

        if (index >= 0) {
            walkers[index] = walker
        }
    }

    override suspend fun getAvailableWalkers(): List<Walker> {
        return walkers.filter { it.available }
    }
}