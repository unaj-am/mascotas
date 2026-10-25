package com.app.features.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.PetOwnerLavender
import com.app.core.ui.theme.PetOwnerPurple

@Composable
fun HomeStatCard(
    title: String,
    value: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = PetOwnerLavender
        )
    ) {
        Column(
            modifier = Modifier.padding(DimensUI.SpaceLarge),
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceSmall
            )
        ) {
            Box(
                modifier = Modifier.size(DimensUI.IconSizeLarge),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PetOwnerPurple
                )
            }

            Text(
                text = value.toString(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}