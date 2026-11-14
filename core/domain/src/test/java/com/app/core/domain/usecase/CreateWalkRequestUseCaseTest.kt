package com.app.core.domain.usecase

import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.Walker
import com.app.core.domain.repository.WalkRequestRepository
import com.app.core.domain.repository.WalkerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CreateWalkRequestUseCaseTest {

    @Test
    fun invoke_rejectsUnavailableWalkerBeforePersisting() = runBlocking {
        val requests = RecordingWalkRequestRepository()
        val useCase = CreateWalkRequestUseCase(
            walkRequestRepository = requests,
            walkerRepository = FakeWalkerRepository(available = false)
        )

        val error = runCatching { useCase(request()) }.exceptionOrNull()

        assertEquals(
            "Este paseador ya no está disponible para recibir solicitudes.",
            error?.message
        )
        assertNull(requests.createdRequest)
    }

    @Test
    fun invoke_persistsRequestWhenWalkerIsAvailable() = runBlocking {
        val requests = RecordingWalkRequestRepository()
        val useCase = CreateWalkRequestUseCase(
            walkRequestRepository = requests,
            walkerRepository = FakeWalkerRepository(available = true)
        )
        val expected = request()

        useCase(expected)

        assertEquals(expected, requests.createdRequest)
    }

    private fun request() = WalkRequest(
        id = "request-1",
        ownerId = "owner-1",
        walkerId = "walker-1",
        petIds = listOf("pet-1"),
        scheduledAt = "2026-11-10T10:00",
        durationMinutes = 60,
        status = RequestStatus.PENDING
    )

    private class FakeWalkerRepository(
        private val available: Boolean
    ) : WalkerRepository {
        override suspend fun getWalkers() = emptyList<Walker>()
        override suspend fun getWalkerById(userId: String) =
            Walker(userId, "", 0, "", available = available)
        override suspend fun saveWalker(walker: Walker) = Unit
        override suspend fun updateWalker(walker: Walker) = Unit
        override suspend fun getAvailableWalkers() = emptyList<Walker>()
        override suspend fun updateAvailability(userId: String, available: Boolean) = Unit
    }

    private class RecordingWalkRequestRepository : WalkRequestRepository {
        var createdRequest: WalkRequest? = null

        override suspend fun getRequestById(requestId: String): WalkRequest? = null
        override suspend fun getRequestsByOwner(ownerId: String) = emptyList<WalkRequest>()
        override suspend fun getRequestsByWalker(walkerId: String) = emptyList<WalkRequest>()
        override suspend fun createRequest(request: WalkRequest) {
            createdRequest = request
        }
        override suspend fun updateRequestStatus(requestId: String, status: RequestStatus) = Unit
        override suspend fun deleteRequest(requestId: String) = Unit
        override suspend fun acceptRequestAndCreateWalk(requestId: String): Walk =
            error("Not used in this test")
    }
}
