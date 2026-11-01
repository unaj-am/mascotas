
package com.app.core.domain.model

data class WalkRequest(
    val id: String,
    val ownerId: String,
    val walkerId: String,
    val petIds: List<String>,
    val scheduledAt: String,
    val durationMinutes: Int,
    val status: RequestStatus = RequestStatus.PENDING,
    val observations: String = "",

    // Datos para mostrar la solicitud sin consultar documentos privados.
    val ownerName: String = "",
    val petNames: List<String> = emptyList(),
    val petPhotos: List<String> = emptyList()
)
