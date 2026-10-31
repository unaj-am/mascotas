
package com.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.app.core.ui.theme.DimensUI
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
/**
 * Bloque visual de carga.
 *
 * No ejecuta animaciones ni operaciones de red.
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    height: Dp = DimensUI.SpaceLarge,
    width: Dp? = null,
    circular: Boolean = false
) {
    val shape = if (circular) {
        CircleShape
    } else {
        RoundedCornerShape(10.dp)
    }

    Box(
        modifier = modifier
            .then(
                if (width != null) Modifier.width(width)
                else Modifier.fillMaxWidth()
            )
            .height(height)
            .clip(shape)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.75f
                )
            )
    )
}

/**
 * Esqueleto para tarjetas de contenido.
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceMedium
            )
        ) {
            SkeletonBox(
                modifier = Modifier.size(
                    DimensUI.AvatarSizeLarge
                ),
                height = DimensUI.AvatarSizeLarge,
                circular = true
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceSmall
                )
            ) {
                SkeletonBox(
                    width = 130.dp,
                    height = 18.dp
                )

                SkeletonBox(
                    height = 14.dp
                )

                SkeletonBox(
                    width = 90.dp,
                    height = 14.dp
                )
            }
        }
    }
}

/**
 * Esqueleto para listas.
 */
@Composable
fun SkeletonListContent(
    modifier: Modifier = Modifier,
    itemCount: Int = 4
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {
        repeat(itemCount.coerceAtLeast(0)) {
            SkeletonCard()
        }
    }
}
