package com.app.core.domain.model

fun Walk.validateStatusChange(userId: String, newStatus: WalkStatus) {
    require(ownerId == userId || walkerId == userId) {
        "No tenés permiso para modificar este paseo"
    }

    when (newStatus) {
        WalkStatus.IN_PROGRESS -> {
            require(walkerId == userId) {
                "Solo el paseador puede iniciar el paseo"
            }
            require(status == WalkStatus.SCHEDULED) {
                "Solo se pueden iniciar paseos programados"
            }
        }

        WalkStatus.COMPLETED -> {
            require(walkerId == userId) {
                "Solo el paseador puede finalizar el paseo"
            }
            require(status == WalkStatus.IN_PROGRESS) {
                "Solo se pueden finalizar paseos en curso"
            }
        }

        WalkStatus.CANCELLED -> {
            require(ownerId == userId) {
                "Solo el dueño puede cancelar este paseo"
            }
            require(status == WalkStatus.SCHEDULED) {
                "Solo se pueden cancelar paseos programados"
            }
        }

        WalkStatus.SCHEDULED -> {
            throw IllegalArgumentException(
                "No se puede volver al estado programado"
            )
        }
    }
}
