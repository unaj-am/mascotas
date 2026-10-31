
package com.app.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    durationMillis: Int = 220,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(durationMillis)
        ) + slideInVertically(
            animationSpec = tween(durationMillis),
            initialOffsetY = { fullHeight -> fullHeight / 8 }
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis)
        ) + slideOutVertically(
            animationSpec = tween(durationMillis),
            targetOffsetY = { fullHeight -> fullHeight / 8 }
        )
    ) {
        content()
    }
}
