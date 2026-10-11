package com.app.core.data.repository

import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.repository.WalkRequestRepository

class WalkRequestRepositoryImpl : WalkRequestRepository {

    private val requests = mutableListOf<WalkRequest>()

    override suspend fun getRequestById(
        requestId: String
    ): WalkRequest? {
        return requests.find { it.id == requestId }
    }

    override suspend fun getRequestsByOwner(
        ownerId: String
    ): List<WalkRequest> {
        return requests.filter { it.ownerId == ownerId }
    }

    override suspend fun getRequestsByWalker(
        walkerId: String
    ): List<WalkRequest> {
        return requests.filter { it.walkerId == walkerId }
    }

    override suspend fun createRequest(
        request: WalkRequest
    ) {
        val exists = requests.any { it.id == request.id }

        if (!exists) {
            requests.add(request)
        }
    }

    override suspend fun updateRequestStatus(
        requestId: String,
        status: RequestStatus
    ) {
        val index = requests.indexOfFirst {
            it.id == requestId
        }

        if (index >= 0) {
            requests[index] = requests[index].copy(
                status = status
            )
        }
    }
}