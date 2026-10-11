package com.app.core.domain.model

data class WalkRequest(
    val id: String,
    val ownerId: String,
    val walkerId: String,
    val petIds: List<String>,
    val scheduledAt: String,
    val durationMinutes: Int,
    val status: RequestStatus = RequestStatus.PENDING,
    val observations: String = ""
)