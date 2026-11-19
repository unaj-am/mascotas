package com.app.features.walks.walker

import androidx.lifecycle.ViewModelStore
import com.app.core.domain.model.Pet
import com.app.core.domain.model.User
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.repository.PetRepository
import com.app.core.domain.repository.UserRepository
import com.app.core.domain.repository.WalkObservationRepository
import com.app.core.domain.repository.WalkRepository
import com.app.core.domain.usecase.CompleteWalkUseCase
import com.app.core.domain.usecase.GetPetByIdUseCase
import com.app.core.domain.usecase.GetUserByIdUseCase
import com.app.core.domain.usecase.StartWalkUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WalkerWalkActionsTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun trackingCallbacksWaitForStartAndCompletionConfirmation() = runTest(dispatcher) {
        for (starting in listOf(true, false)) {
            val repository = DeferredWalkRepository()
            val viewModel = viewModel(repository, starting.toString())
            viewModel.load("walker")
            runCurrent()
            var trackingCallbackCalled = false

            if (starting) {
                viewModel.start("walk") { trackingCallbackCalled = true }
            } else {
                viewModel.complete("walk") { trackingCallbackCalled = true }
            }
            runCurrent()

            assertFalse(trackingCallbackCalled)
            assertEquals("walk", viewModel.uiState.value.processingId)

            repository.confirmation.complete(Unit)
            runCurrent()

            assertTrue(trackingCallbackCalled)
            assertEquals(null, viewModel.uiState.value.processingId)
        }
    }

    @Test
    fun failedStartOrCompletionDoesNotInvokeTrackingCallback() = runTest(dispatcher) {
        for (starting in listOf(true, false)) {
            val repository = DeferredWalkRepository()
            val viewModel = viewModel(repository, starting.toString())
            viewModel.load("walker")
            runCurrent()
            var trackingCallbackCalled = false

            if (starting) {
                viewModel.start("walk") { trackingCallbackCalled = true }
            } else {
                viewModel.complete("walk") { trackingCallbackCalled = true }
            }
            runCurrent()
            repository.confirmation.completeExceptionally(
                IllegalStateException("No se pudo confirmar")
            )
            runCurrent()

            assertFalse(trackingCallbackCalled)
            assertEquals("No se pudo confirmar", viewModel.uiState.value.operationError)
        }
    }

    private fun viewModel(repository: WalkRepository, key: String): WalkerWalksViewModel =
        WalkerWalksViewModel(
            object : WalkObservationRepository {
                override fun observeWalk(walkId: String) = flowOf<Walk?>(null)
                override fun observeWalksByOwner(ownerId: String) = flowOf(emptyList<Walk>())
                override fun observeWalksByWalker(walkerId: String) = flowOf(emptyList<Walk>())
                override fun observeRequestsByOwner(ownerId: String) = flowOf(emptyList<WalkRequest>())
                override fun observeRequestsByWalker(walkerId: String) = flowOf(emptyList<WalkRequest>())
                override fun observeRequest(requestId: String) = flowOf<WalkRequest?>(null)
            },
            StartWalkUseCase(repository),
            CompleteWalkUseCase(repository),
            GetUserByIdUseCase(object : UserRepository {
                override suspend fun getUserById(userId: String): User? = null
                override suspend fun saveUser(user: User) = error("Escritura inesperada")
                override suspend fun updateUser(user: User) = error("Escritura inesperada")
                override suspend fun updateProfileImage(userId: String, imageUrl: String) =
                    error("Escritura inesperada")
            }),
            GetPetByIdUseCase(object : PetRepository {
                override suspend fun getPetsByOwner(ownerId: String) = emptyList<Pet>()
                override suspend fun getPetById(petId: String): Pet? = null
                override suspend fun savePet(pet: Pet) = error("Escritura inesperada")
                override suspend fun deletePet(petId: String) = error("Escritura inesperada")
            })
        ).also { store.put(key, it) }

    private class DeferredWalkRepository : WalkRepository {
        val confirmation = CompletableDeferred<Unit>()
        override suspend fun startWalk(walkId: String) = confirmation.await()
        override suspend fun completeWalk(walkId: String) = confirmation.await()
        override suspend fun cancelWalk(walkId: String) = confirmation.await()
        override suspend fun getWalkById(walkId: String): Walk? = error("Lectura inesperada")
        override suspend fun getWalksByOwner(ownerId: String) = emptyList<Walk>()
        override suspend fun getWalksByWalker(walkerId: String) = emptyList<Walk>()
        override suspend fun saveWalk(walk: Walk) = error("Escritura inesperada")
    }
}
