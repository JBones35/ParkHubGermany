package com.parkhub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
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

            // Titel + Suche
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
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Suchen",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            // Segmented Button: Fahrzeuge / Fahrer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                tabs.forEachIndexed { index, title ->
                    OutlinedButton(
                        onClick = { selectedTab = index },
                        modifier = Modifier.weight(1f),
                        shape = when (index) {
                            0 -> RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
                            else -> RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedTab == index) ParkHubGreenContainer else Color.Transparent,
                            contentColor = if (selectedTab == index) ParkHubGreen else Gray
                        )
                    ) {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips: Alle / Aktiv / In Wartung
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
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ParkHubGreenContainer,
                            selectedLabelColor = ParkHubGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fahrzeugliste (Nutzt das ausgelagerte FahrzeugItem)
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

        // FAB
        FloatingActionButton(
            onClick = { /* Fahrzeug hinzufügen */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = ParkHubGreen,
            contentColor = White
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Fahrzeug hinzufügen")
        }
    }
}