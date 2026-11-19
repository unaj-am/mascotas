package com.app.core.domain.repository

import com.app.core.domain.model.Walk

interface WalkRepository {

    suspend fun getWalkById(
        walkId: String
    ): Walk?

    suspend fun getWalksByOwner(
        ownerId: String
    ): List<Walk>

    suspend fun getWalksByWalker(
        walkerId: String
    ): List<Walk>

    suspend fun saveWalk(
        walk: Walk
    )

    suspend fun startWalk(walkId: String)

    suspend fun completeWalk(walkId: String)

    suspend fun cancelWalk(walkId: String)
}