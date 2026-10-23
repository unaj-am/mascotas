package com.app.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.core.ui.theme.DimensUI
import com.app.features.home.component.HomeHeader
import com.app.features.home.component.HomePetsCard
import com.app.features.home.component.HomeQuickAction
import com.app.features.home.component.HomeSectionHeader
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
    onPetsClick: () -> Unit,
    onWalksClick: () -> Unit,
    onFindWalkerClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = DimensUI.ScreenHorizontalPadding,
            vertical = DimensUI.SpaceLarge
        ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceXLarge
        )
    ) {
        item {
            HomeHeader(
                userName = userName,
                onNotificationsClick = onNotificationsClick
            )
        }

        item {
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

        item {
            NextWalkCard(
                petName = nextPetName,
                petBreed = nextPetBreed,
                walkDate = nextWalkDate,
                onFindWalkerClick = onFindWalkerClick
            )
        }

        item {
            HomeSectionHeader(
                title = "ACCIONES RÁPIDAS"
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceSmall
                )
            ) {
                HomeQuickAction(
                    modifier = Modifier.weight(1f),
                    title = "Mascotas",
                    icon = Icons.Filled.Pets,
                    onClick = onPetsClick
                )

                HomeQuickAction(
                    modifier = Modifier.weight(1f),
                    title = "Paseadores",
                    icon = Icons.Filled.Search,
                    onClick = onFindWalkerClick
                )

                HomeQuickAction(
                    modifier = Modifier.weight(1f),
                    title = "Historial",
                    icon = Icons.Filled.History,
                    onClick = onHistoryClick
                )
            }
        }

        item {
            HomeSectionHeader(
                title = "MIS MASCOTAS",
                action = "Ver todas",
                onAction = onPetsClick
            )
        }

        item {
            HomePetsCard(
                petsCount = petsCount,
                onClick = onPetsClick
            )
        }
    }
}