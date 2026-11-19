package com.app.core.domain.usecase

import com.app.core.domain.model.Walk
import com.app.core.domain.repository.WalkRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WalkActionsUseCaseTest {
    private val repository = RecordingWalkRepository()

    @Test
    fun callsTheExplicitRepositoryActionWithoutAnExtraRead() = runBlocking {
        StartWalkUseCase(repository)("walk-start")
        CompleteWalkUseCase(repository)("walk-complete")
        CancelWalkUseCase(repository)("walk-cancel")

        assertEquals(
            listOf("start:walk-start", "complete:walk-complete", "cancel:walk-cancel"),
            repository.actions
        )
    }

    @Test
    fun rejectsBlankIdentifiersBeforeCallingTheRepository() = runBlocking {
        val failures = listOf(
            runCatching { StartWalkUseCase(repository)(" ") },
            runCatching { CompleteWalkUseCase(repository)("") },
            runCatching { CancelWalkUseCase(repository)(" ") }
        )

        assertTrue(failures.all { it.exceptionOrNull() is IllegalArgumentException })
        assertTrue(repository.actions.isEmpty())
    }

    @Test
    fun propagatesAnUnconfirmedOperationInsteadOfReportingSuccess() = runBlocking {
        repository.failure = IllegalStateException("No se pudo confirmar la transacción")

        val failures = listOf(
            runCatching { StartWalkUseCase(repository)("walk") },
            runCatching { CompleteWalkUseCase(repository)("walk") },
            runCatching { CancelWalkUseCase(repository)("walk") }
        )

        assertTrue(failures.all { it.exceptionOrNull() === repository.failure })
    }

    private class RecordingWalkRepository : WalkRepository {
        val actions = mutableListOf<String>()
        var failure: Exception? = null

        override suspend fun startWalk(walkId: String) = record("start", walkId)
        override suspend fun completeWalk(walkId: String) = record("complete", walkId)
        override suspend fun cancelWalk(walkId: String) = record("cancel", walkId)

        private fun record(action: String, walkId: String) {
            failure?.let { throw it }
            actions.add("$action:$walkId")
        }

        override suspend fun getWalkById(walkId: String): Walk? =
            error("No debe consultar antes de la transacción")
        override suspend fun getWalksByOwner(ownerId: String): List<Walk> =
            error("Consulta inesperada")
        override suspend fun getWalksByWalker(walkerId: String): List<Walk> =
            error("Consulta inesperada")
        override suspend fun saveWalk(walk: Walk) = error("Escritura inesperada")
    }
}
