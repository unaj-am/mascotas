package com.app.core.domain.repository

import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import kotlinx.coroutines.flow.Flow

interface WalkObservationRepository {
    fun observeWalk(walkId: String): Flow<Walk?>
    fun observeWalksByOwner(ownerId: String): Flow<List<Walk>>
    fun observeWalksByWalker(walkerId: String): Flow<List<Walk>>
    fun observeRequestsByOwner(ownerId: String): Flow<List<WalkRequest>>
    fun observeRequestsByWalker(walkerId: String): Flow<List<WalkRequest>>
    fun observeRequest(requestId: String): Flow<WalkRequest?>
}
