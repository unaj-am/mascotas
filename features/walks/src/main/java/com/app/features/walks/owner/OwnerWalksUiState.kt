
package com.app.features.walks.owner

import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.WalkStatus

data class OwnerWalksUiState(
    val walks: List<Walk> = emptyList(),
    val requests: List<WalkRequest> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val operationMessage: String? = null,
    val operationError: String? = null,
    val processingId: String? = null,
    val selectedFilter: OwnerWalkFilter = OwnerWalkFilter.ALL
) {
    val filteredWalks: List<Walk>
        get() = when (selectedFilter) {
            OwnerWalkFilter.ALL -> walks

            OwnerWalkFilter.SCHEDULED ->
                walks.filter { it.status == WalkStatus.SCHEDULED }

            OwnerWalkFilter.IN_PROGRESS ->
                walks.filter { it.status == WalkStatus.IN_PROGRESS }

            OwnerWalkFilter.COMPLETED ->
                walks.filter { it.status == WalkStatus.COMPLETED }

            OwnerWalkFilter.CANCELLED ->
                walks.filter { it.status == WalkStatus.CANCELLED }

            OwnerWalkFilter.PENDING,
            OwnerWalkFilter.REJECTED -> emptyList()
        }

    val filteredRequests: List<WalkRequest>
        get() = when (selectedFilter) {
            OwnerWalkFilter.ALL -> requests

            OwnerWalkFilter.PENDING ->
                requests.filter { it.status == RequestStatus.PENDING }

            OwnerWalkFilter.REJECTED ->
                requests.filter { it.status == RequestStatus.REJECTED }

            OwnerWalkFilter.CANCELLED ->
                requests.filter { it.status == RequestStatus.CANCELLED }

            else -> emptyList()
        }
}
