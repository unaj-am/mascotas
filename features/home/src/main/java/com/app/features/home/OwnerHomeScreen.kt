package com.app.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.PetOwnerLavender
import com.app.core.ui.theme.PetOwnerPurple
import com.app.core.ui.theme.PetTextOnDark

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceXSmall
                    )
                ) {
                    Text(
                        text = "HOLA DE NUEVO",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "¿Listos para el próximo paseo?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onNotificationsClick) {
                    Surface(
                        modifier = Modifier.size(DimensUI.IconSizeLarge),
                        shape = CircleShape,
                        color = PetOwnerLavender
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsNone,
                                contentDescription = "Notificaciones",
                                tint = PetOwnerPurple
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                OwnerStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Próximos paseos",
                    value = upcomingWalksCount.toString(),
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null
                        )
                    }
                )

                OwnerStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Mis mascotas",
                    value = petsCount.toString(),
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Pets,
                            contentDescription = null
                        )
                    }
                )
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                SectionHeader(title = "PRÓXIMO PASEO")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = PetOwnerPurple
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = DimensUI.SpaceXSmall
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(
                            DimensUI.SpaceXLarge
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceMedium
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                DimensUI.SpaceSmall
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WatchLater,
                                contentDescription = null,
                                tint = PetTextOnDark
                            )

                            Text(
                                text = "Paseo programado",
                                style = MaterialTheme.typography.labelLarge,
                                color = PetTextOnDark
                            )
                        }

                        if (nextPetName != null) {
                            Text(
                                text = nextPetName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = PetTextOnDark
                            )

                            if (!nextPetBreed.isNullOrBlank()) {
                                Text(
                                    text = nextPetBreed,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PetTextOnDark
                                )
                            }

                            if (!nextWalkDate.isNullOrBlank()) {
                                Text(
                                    text = nextWalkDate,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PetTextOnDark
                                )
                            }
                        } else {
                            Text(
                                text = "Todavía no tenés paseos programados",
                                style = MaterialTheme.typography.titleMedium,
                                color = PetTextOnDark
                            )

                            Text(
                                text = "Buscá un paseador para organizar el próximo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PetTextOnDark
                            )
                        }

                        Surface(
                            onClick = onFindWalkerClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    DimensUI.SpaceMedium
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Buscar paseador",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PetOwnerPurple
                                )

                                Spacer(
                                    Modifier.size(DimensUI.SpaceSmall)
                                )

                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = PetOwnerPurple
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                SectionHeader(title = "ACCIONES RÁPIDAS")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        DimensUI.SpaceSmall
                    )
                ) {
                    OwnerQuickAction(
                        modifier = Modifier.weight(1f),
                        title = "Mascotas",
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Pets,
                                contentDescription = null
                            )
                        },
                        onClick = onPetsClick
                    )

                    OwnerQuickAction(
                        modifier = Modifier.weight(1f),
                        title = "Paseadores",
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                            )
                        },
                        onClick = onFindWalkerClick
                    )

                    OwnerQuickAction(
                        modifier = Modifier.weight(1f),
                        title = "Historial",
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = null
                            )
                        },
                        onClick = onHistoryClick
                    )
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceMedium
                )
            ) {
                SectionHeader(
                    title = "MIS MASCOTAS",
                    action = "Ver todas",
                    onAction = onPetsClick
                )

                Card(
                    onClick = onPetsClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(
                            DimensUI.SpaceLarge
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            DimensUI.SpaceMedium
                        )
                    ) {
                        Surface(
                            modifier = Modifier.size(
                                DimensUI.PetAvatarSize
                            ),
                            shape = CircleShape,
                            color = PetOwnerLavender
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Pets,
                                    contentDescription = null,
                                    tint = PetOwnerPurple,
                                    modifier = Modifier.size(
                                        DimensUI.IconSizeLarge
                                    )
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(
                                DimensUI.SpaceXSmall
                            )
                        ) {
                            Text(
                                text = if (petsCount == 0) {
                                    "Registrá tu primera mascota"
                                } else {
                                    "Tenés $petsCount mascotas registradas"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Consultá sus perfiles y datos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "Ver mascotas",
                            tint = PetOwnerPurple
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OwnerStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: @Composable () -> Unit
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
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides PetOwnerPurple
                ) {
                    icon()
                }
            }

            Text(
                text = value,
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

@Composable
private fun SectionHeader(
    title: String,
    action: String? = null,
    onAction: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )

        if (action != null) {
            androidx.compose.material3.TextButton(
                onClick = onAction,
                contentPadding = PaddingValues(
                    horizontal = DimensUI.SpaceSmall,
                    vertical = DimensUI.SpaceXSmall
                )
            ) {
                Text(
                    text = action,
                    style = MaterialTheme.typography.labelLarge,
                    color = PetOwnerPurple
                )
            }
        }
    }
}

@Composable
private fun OwnerQuickAction(
    modifier: Modifier = Modifier,
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(DimensUI.QuickActionHeight),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DimensUI.SpaceXXSmall
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(DimensUI.SpaceSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(DimensUI.IconSizeLarge),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides PetOwnerPurple
                ) {
                    icon()
                }
            }

            Spacer(Modifier.height(DimensUI.SpaceXSmall))

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
