package com.app.core.domain.repository

import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkStatus

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

    suspend fun updateWalkStatus(
        walkId: String,
        status: WalkStatus
    )
}