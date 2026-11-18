package com.app.features.walks.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.app.core.domain.model.*
import com.app.core.domain.repository.*
import com.app.core.domain.usecase.CancelWalkUseCase
import com.app.core.domain.usecase.GetUserByIdUseCase
import com.app.core.domain.usecase.UpdateWalkRequestStatusUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LiveWalkUpdatesTest {
    private val dispatcher = StandardTestDispatcher()
    private val viewModelStore = ViewModelStore()
    private val observations = FakeObservations()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun acceptedRequestBecomesScheduledWalkWithoutReloading() = runTest(dispatcher) {
        val request = request()
        observations.requests("owner").value = listOf(request)
        val viewModel = ownerViewModel()

        viewModel.load("owner")
        runCurrent()
        assertEquals(listOf(request), viewModel.uiState.value.requests)

        observations.walks("owner").value = listOf(walk())
        observations.requests("owner").value = listOf(request.copy(status = RequestStatus.ACCEPTED))
        runCurrent()

        assertTrue(viewModel.uiState.value.requests.isEmpty())
        assertEquals(WalkStatus.SCHEDULED, viewModel.uiState.value.walks.single().status)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun activeWalkBecomesCompletedInListAndDetailWithoutReloading() = runTest(dispatcher) {
        val activeWalk = walk().copy(status = WalkStatus.IN_PROGRESS)
        observations.walks("owner").value = listOf(activeWalk)
        observations.walk.value = activeWalk
        val list = ownerViewModel()
        val detail = OwnerWalkDetailViewModel(
            observations,
            GetUserByIdUseCase(object : UserRepository {
                override suspend fun getUserById(userId: String): User? = null
                override suspend fun saveUser(user: User) = error("Unexpected write")
                override suspend fun updateUser(user: User) = error("Unexpected write")
                override suspend fun updateProfileImage(userId: String, imageUrl: String) =
                    error("Unexpected write")
            })
        )
        track("detail", detail)
        list.load("owner")
        detail.load(activeWalk.id)
        runCurrent()

        assertEquals(WalkStatus.IN_PROGRESS, detail.uiState.value.walk?.status)

        val completed = activeWalk.copy(
            status = WalkStatus.COMPLETED,
            finishedAt = "2026-11-18T12:30:00Z"
        )
        observations.walks("owner").value = listOf(completed)
        observations.walk.value = completed
        runCurrent()

        assertEquals(completed, list.uiState.value.walks.single())
        assertEquals(completed, detail.uiState.value.walk)
        assertFalse(detail.uiState.value.isLoading)
    }

    @Test
    fun switchingOwnerCancelsPreviousSubscriptionAndIgnoresOldUpdates() = runTest(dispatcher) {
        observations.walks("owner").value = listOf(walk())
        val viewModel = ownerViewModel()
        viewModel.load("owner")
        runCurrent()

        viewModel.load("other")
        runCurrent()
        observations.walks("owner").value = listOf(walk().copy(status = WalkStatus.COMPLETED))
        runCurrent()

        assertTrue(viewModel.uiState.value.walks.isEmpty())
        assertTrue(viewModel.uiState.value.requests.isEmpty())
        assertEquals(0, observations.walks("owner").subscriptionCount.value)
        assertEquals(1, observations.walks("other").subscriptionCount.value)
    }

    private fun ownerViewModel(): OwnerWalksViewModel =
        OwnerWalksViewModel(
            observations,
            CancelWalkUseCase(UnusedWalkRepository()),
            UpdateWalkRequestStatusUseCase(UnusedRequestRepository())
        ).also { track("list", it) }

    private fun track(key: String, viewModel: ViewModel) {
        viewModelStore.put(key, viewModel)
    }

    private fun walk() = Walk(
        id = "request",
        ownerId = "owner",
        walkerId = "walker",
        petIds = listOf("pet"),
        scheduledAt = "2026-11-18T12:00:00Z",
        durationMinutes = 30
    )

    private fun request() = WalkRequest(
        id = "request",
        ownerId = "owner",
        walkerId = "walker",
        petIds = listOf("pet"),
        scheduledAt = "2026-11-18T12:00:00Z",
        durationMinutes = 30
    )

    private class FakeObservations : WalkObservationRepository {
        private val ownerWalks = mutableMapOf<String, MutableStateFlow<List<Walk>>>()
        private val ownerRequests = mutableMapOf<String, MutableStateFlow<List<WalkRequest>>>()
        val walk = MutableStateFlow<Walk?>(null)

        fun walks(userId: String) =
            ownerWalks.getOrPut(userId) { MutableStateFlow(emptyList()) }
        fun requests(userId: String) =
            ownerRequests.getOrPut(userId) { MutableStateFlow(emptyList()) }

        override fun observeWalk(walkId: String): Flow<Walk?> = walk
        override fun observeWalksByOwner(ownerId: String): Flow<List<Walk>> = walks(ownerId)
        override fun observeRequestsByOwner(ownerId: String): Flow<List<WalkRequest>> = requests(ownerId)
        override fun observeWalksByWalker(walkerId: String): Flow<List<Walk>> = flowOf(emptyList())
        override fun observeRequestsByWalker(walkerId: String): Flow<List<WalkRequest>> = flowOf(emptyList())
        override fun observeRequest(requestId: String): Flow<WalkRequest?> = flowOf(null)
    }

    // These operations must not be called while merely observing remote changes.
    private class UnusedWalkRepository : WalkRepository {
        override suspend fun getWalkById(walkId: String): Walk? = error("Unexpected read")
        override suspend fun getWalksByOwner(ownerId: String): List<Walk> = error("Unexpected read")
        override suspend fun getWalksByWalker(walkerId: String): List<Walk> = error("Unexpected read")
        override suspend fun saveWalk(walk: Walk) = error("Unexpected write")
        override suspend fun updateWalkStatus(walkId: String, status: WalkStatus) =
            error("Unexpected write")
    }

    private class UnusedRequestRepository : WalkRequestRepository {
        override suspend fun getRequestById(requestId: String): WalkRequest? = error("Unexpected read")
        override suspend fun getRequestsByOwner(ownerId: String): List<WalkRequest> = error("Unexpected read")
        override suspend fun getRequestsByWalker(walkerId: String): List<WalkRequest> = error("Unexpected read")
        override suspend fun createRequest(request: WalkRequest) = error("Unexpected write")
        override suspend fun updateRequestStatus(requestId: String, status: RequestStatus) =
            error("Unexpected write")
        override suspend fun deleteRequest(requestId: String) = error("Unexpected write")
        override suspend fun acceptRequestAndCreateWalk(requestId: String): Walk = error("Unexpected write")
    }
}
