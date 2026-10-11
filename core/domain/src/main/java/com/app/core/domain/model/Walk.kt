package com.app.core.domain.model

data class Walk(
    val id: String,
    val ownerId: String,
    val walkerId: String,
    val petIds: List<String>,
    val scheduledAt: String,
    val durationMinutes: Int,
    val status: WalkStatus = WalkStatus.SCHEDULED,
    val startedAt: String? = null,
    val finishedAt: String? = null,
    val distanceKm: Double? = null,
    val observations: String = ""
)