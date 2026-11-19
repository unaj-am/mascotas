package com.app.core.domain.model

import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class WalkStatusRulesTest(
    private val currentStatus: WalkStatus,
    private val newStatus: WalkStatus,
    private val userId: String,
    private val allowed: Boolean
) {
    @Test
    fun validatesActorAndTransition() {
        val walk = Walk(
            id = "walk",
            ownerId = "owner",
            walkerId = "walker",
            petIds = listOf("pet"),
            scheduledAt = "2026-11-19T12:00:00Z",
            durationMinutes = 30,
            status = currentStatus
        )

        if (allowed) {
            walk.validateStatusChange(userId, newStatus)
        } else {
            assertThrows(IllegalArgumentException::class.java) {
                walk.validateStatusChange(userId, newStatus)
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0} -> {1}, actor={2}, allowed={3}")
        fun transitions(): List<Array<Any>> =
            WalkStatus.entries.flatMap { current ->
                WalkStatus.entries.flatMap { next ->
                    listOf("owner", "walker", "stranger").map { userId ->
                        val allowed = when (next) {
                            WalkStatus.IN_PROGRESS ->
                                current == WalkStatus.SCHEDULED && userId == "walker"
                            WalkStatus.COMPLETED ->
                                current == WalkStatus.IN_PROGRESS && userId == "walker"
                            WalkStatus.CANCELLED ->
                                current == WalkStatus.SCHEDULED && userId == "owner"
                            WalkStatus.SCHEDULED -> false
                        }
                        arrayOf<Any>(current, next, userId, allowed)
                    }
                }
            }
    }
}
