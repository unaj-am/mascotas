
package com.app.features.home.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Pets
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.components.AppAnimatedVisibility
import com.app.core.ui.theme.DimensUI
import com.app.features.home.component.HomeHeader
import com.app.features.home.component.HomeStatCard
import com.app.features.home.component.NextWalkCard

@Composable
fun OwnerHomeScreen(
    userName: String,
    petsCount: Int,
    upcomingWalksCount: Int,
    nextPetName: String? = null,
    nextPetBreed: String? = null,
    nextWalkDate: String? = null,
    onNotificationsClick: () -> Unit,
    onFindWalkerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = DimensUI.ScreenHorizontalPadding,
            vertical = DimensUI.ScreenVerticalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SectionSpacing
        )
    ) {
        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 260
            ) {
                HomeHeader(
                    userName = userName,
                    onNotificationsClick = onNotificationsClick
                )
            }
        }

        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 300
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceMedium
                    )
                ) {
                    HomeStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Próximos paseos",
                        value = upcomingWalksCount,
                        icon = Icons.Filled.CalendarMonth
                    )

                    HomeStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Mis mascotas",
                        value = petsCount,
                        icon = Icons.Filled.Pets
                    )
                }
            }
        }

        item {
            AppAnimatedVisibility(
                visible = true,
                durationMillis = 340
            ) {
                NextWalkCard(
                    petName = nextPetName,
                    petBreed = nextPetBreed,
                    walkDate = nextWalkDate,
                    onFindWalkerClick = onFindWalkerClick
                )
            }
        }
    }
}
