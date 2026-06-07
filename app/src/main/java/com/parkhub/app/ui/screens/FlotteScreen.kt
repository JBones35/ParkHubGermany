package com.parkhub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.model.FahrzeugStatus
import com.parkhub.app.model.fahrzeugListe
import com.parkhub.app.ui.components.flotte.FahrzeugItem
import com.parkhub.app.ui.theme.*

@Composable
fun FlotteScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableStateOf(0) }

    val tabs = listOf("Fahrzeuge (12)", "Fahrer (9)")
    val filter = listOf("Alle", "Aktiv", "In Wartung")

    val gefilterteListe = when (selectedFilter) {
        1 -> fahrzeugListe.filter { it.status == FahrzeugStatus.AKTIV }
        2 -> fahrzeugListe.filter { it.status == FahrzeugStatus.WARTUNG }
        else -> fahrzeugListe
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Flotte",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Suchen",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    SegmentedButton(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = tabs.size
                        ),
                        icon = {},
                        border = SegmentedButtonDefaults.borderStroke(
                            color = Color.Transparent
                        ),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = White,
                            activeContentColor = MaterialTheme.colorScheme.onSurface,
                            activeBorderColor = Color.Transparent,
                            inactiveContainerColor = Color(0xFFE8E8E8),
                            inactiveContentColor = Gray,
                            inactiveBorderColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == index)
                                FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filter.forEachIndexed { index, label ->
                    FilterChip(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        label = { Text(text = label, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ParkHubGreenContainer,
                            selectedLabelColor = ParkHubGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gefilterteListe) { fahrzeug ->
                    FahrzeugItem(fahrzeug = fahrzeug)
                }
            }
        }

        FloatingActionButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = ParkHubGreen,
            contentColor = White
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Fahrzeug hinzufügen"
            )
        }
    }
}