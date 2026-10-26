
package com.app.core.domain.repository

import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest

interface WalkRequestRepository {

    suspend fun getRequestById(
        requestId: String
    ): WalkRequest?

    suspend fun getRequestsByOwner(
        ownerId: String
    ): List<WalkRequest>

    suspend fun getRequestsByWalker(
        walkerId: String
    ): List<WalkRequest>

    suspend fun createRequest(
        request: WalkRequest
    )

    suspend fun updateRequestStatus(
        requestId: String,
        status: RequestStatus
    )

    suspend fun deleteRequest(
        requestId: String
    )

    suspend fun acceptRequestAndCreateWalk(
        requestId: String
    ): Walk
}
