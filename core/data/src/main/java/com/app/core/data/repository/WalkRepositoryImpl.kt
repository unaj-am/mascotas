package com.app.core.data.repository

import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.repository.WalkRepository

class WalkRepositoryImpl : WalkRepository {

    private val walks = mutableListOf<Walk>()

    override suspend fun getWalkById(
        walkId: String
    ): Walk? {
        return walks.find { it.id == walkId }
    }

    override suspend fun getWalksByOwner(
        ownerId: String
    ): List<Walk> {
        return walks.filter { it.ownerId == ownerId }
    }

    override suspend fun getWalksByWalker(
        walkerId: String
    ): List<Walk> {
        return walks.filter { it.walkerId == walkerId }
    }

    override suspend fun saveWalk(
        walk: Walk
    ) {
        val index = walks.indexOfFirst { it.id == walk.id }

        if (index >= 0) {
            walks[index] = walk
        } else {
            walks.add(walk)
        }
    }

    override suspend fun updateWalkStatus(
        walkId: String,
        status: WalkStatus
    ) {
        val index = walks.indexOfFirst { it.id == walkId }

        if (index >= 0) {
            walks[index] = walks[index].copy(
                status = status
            )
        }
    }
}