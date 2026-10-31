
package com.app.features.walks.owner.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.domain.model.Walk
import com.app.core.ui.theme.DimensUI

@Composable
fun OwnerWalkDetailContent(
    walk: Walk,
    walkerName: String?,
    onTrackWalk: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacingCompact
        )
    ) {
        WalkDetailInfoCard(
            walk = walk,
            walkerName = walkerName
        )

        WalkDetailStatusMessage(
            walk = walk,
            onTrackWalk = onTrackWalk
        )
    }
}

