
package com.app.features.walks.owner

enum class OwnerWalkFilter(
    val label: String
) {
    ALL("Todas"),
    PENDING("Pendientes"),
    REJECTED("Rechazadas"),
    SCHEDULED("Confirmados"),
    IN_PROGRESS("En curso"),
    COMPLETED("Finalizados"),
    CANCELLED("Cancelados")
}
