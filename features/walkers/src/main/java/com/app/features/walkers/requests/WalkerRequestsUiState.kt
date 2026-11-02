
package com.app.features.walkers.requests

import com.app.core.domain.model.WalkRequest

data class WalkerRequestsUiState(
    val walkerId: String = "",
    val requests: List<WalkRequest> = emptyList(),

    val ownerNames: Map<String, String> = emptyMap(),
    val petNames: Map<String, List<String>> = emptyMap(),

    val isLoading: Boolean = false,
    val loadingError: String? = null,
    val processingRequestId: String? = null,
    val operationMessage: String? = null,
    val operationError: String? = null,

    val selectedFilter: WalkerRequestFilter =
        WalkerRequestFilter.ALL
) {
    val filteredRequests: List<WalkRequest>
        get() {
            val status = selectedFilter.status

            return if (status == null) {
                requests
            } else {
                requests.filter { request ->
                    request.status == status
                }
            }
        }
}
