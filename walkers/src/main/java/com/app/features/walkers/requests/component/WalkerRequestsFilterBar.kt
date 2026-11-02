
package com.app.features.walkers.requests.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.WalkRequest
import com.app.features.walkers.requests.WalkerRequestFilter

@Composable
fun WalkerRequestsFilterBar(
    requests: List<WalkRequest>,
    selectedFilter: WalkerRequestFilter,
    onFilterSelected: (WalkerRequestFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = WalkerRequestFilter.entries

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val count = when (filter) {
                WalkerRequestFilter.ALL ->
                    requests.size

                else ->
                    requests.count { request ->
                        request.status == filter.status
                    }
            }

            FilterChip(
                selected = selectedFilter == filter,
                onClick = {
                    onFilterSelected(filter)
                },
                label = {
                    Text(
                        text = "${filter.label} ($count)",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
}
