
package com.app.features.walks.owner.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.RequestStatus
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.WalkStatus
import com.app.features.walks.owner.OwnerWalkFilter

@Composable
fun OwnerWalksFilterBar(
    walks: List<Walk>,
    requests: List<WalkRequest>,
    selectedFilter: OwnerWalkFilter,
    onFilterSelected: (OwnerWalkFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(
            rememberScrollState()
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OwnerWalkFilter.values().forEach { filter ->

            val count = when (filter) {
                OwnerWalkFilter.ALL ->
                    walks.size + requests.size

                OwnerWalkFilter.PENDING ->
                    requests.count {
                        it.status == RequestStatus.PENDING
                    }

                OwnerWalkFilter.REJECTED ->
                    requests.count {
                        it.status == RequestStatus.REJECTED
                    }

                OwnerWalkFilter.SCHEDULED ->
                    walks.count {
                        it.status == WalkStatus.SCHEDULED
                    }

                OwnerWalkFilter.IN_PROGRESS ->
                    walks.count {
                        it.status == WalkStatus.IN_PROGRESS
                    }

                OwnerWalkFilter.COMPLETED ->
                    walks.count {
                        it.status == WalkStatus.COMPLETED
                    }

                OwnerWalkFilter.CANCELLED ->
                    walks.count {
                        it.status == WalkStatus.CANCELLED
                    } + requests.count {
                        it.status == RequestStatus.CANCELLED
                    }
            }

            FilterChip(
                selected = selectedFilter == filter,
                onClick = {
                    onFilterSelected(filter)
                },
                label = {
                    Text("${filter.label} ($count)")
                }
            )
        }
    }
}
