
package com.app.features.walkers.requests

import com.app.core.domain.model.RequestStatus

enum class WalkerRequestFilter(
    val label: String,
    val status: RequestStatus?
) {
    ALL(
        label = "Todas",
        status = null
    ),
    PENDING(
        label = "Pendientes",
        status = RequestStatus.PENDING
    ),
    ACCEPTED(
        label = "Aceptadas",
        status = RequestStatus.ACCEPTED
    ),
    REJECTED(
        label = "Rechazadas",
        status = RequestStatus.REJECTED
    ),
    CANCELLED(
        label = "Canceladas",
        status = RequestStatus.CANCELLED
    )
}
