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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkhub.app.data.AppDatabase
import com.parkhub.app.model.FahrerStatus
import com.parkhub.app.model.FahrzeugStatus
import com.parkhub.app.model.PillTab
import com.parkhub.app.ui.components.PillTabRow
import com.parkhub.app.ui.components.flotte.FahrerItem
import com.parkhub.app.ui.components.flotte.FahrzeugItem
import com.parkhub.app.ui.theme.*

@Composable
fun FlotteScreen(
    viewModel: FlotteViewModel = viewModel(
        factory = FlotteViewModelFactory(
            fahrerDao = AppDatabase.getDatabase(LocalContext.current).fahrerDao(),
            fahrzeugDao = AppDatabase.getDatabase(LocalContext.current).fahrzeugDao(),
            fahrzeugTypDao = AppDatabase.getDatabase(LocalContext.current).fahrzeugTypDao()
        )
    )
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableIntStateOf(0) }

    val fahrerListFromDb by viewModel.fahrerList.collectAsState(initial = emptyList())
    val fahrzeugMitTypListe by viewModel.fahrzeugMitTypListe.collectAsState(initial = emptyList())

    val tabs = listOf(
        PillTab("Fahrzeuge (${fahrzeugMitTypListe.size})"),
        PillTab("Fahrer (${fahrerListFromDb.size})")
    )

    val fahrzeugFilter = listOf("Alle", "Aktiv", "In Wartung")
    val fahrerFilter = listOf("Alle", "Frei", "Eingesetzt", "Abwesend")

    val currentFilter = if (selectedTab == 0) fahrzeugFilter else fahrerFilter

    LaunchedEffect(selectedFilter, selectedTab) {
        if (selectedTab == 0) {
            viewModel.setFahrzeugStatusFilter(
                when (selectedFilter) {
                    1 -> FahrzeugStatus.AKTIV
                    2 -> FahrzeugStatus.WARTUNG
                    else -> null
                }
            )
        } else {
            viewModel.setFahrerStatusFilter(
                when (selectedFilter) {
                    1 -> FahrerStatus.FREI
                    2 -> FahrerStatus.EINGESETZT
                    3 -> FahrerStatus.ABWESEND
                    else -> null
                }
            )
        }
    }

    // Beim Tab-Wechsel den Filter wieder auf "Alle" zurücksetzen
    LaunchedEffect(selectedTab) {
        selectedFilter = 0
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

            PillTabRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentFilter.forEachIndexed { index, label ->
                    FilterChip(
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                        label = { Text(text = label, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer
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
                when (selectedTab) {
                    0 -> items(fahrzeugMitTypListe) { fahrzeugMitTyp ->
                        FahrzeugItem(
                            fahrzeug = fahrzeugMitTyp.fahrzeug,
                            fahrzeugTyp = fahrzeugMitTyp.typ
                        )
                    }
                    1 -> items(fahrerListFromDb) { fahrer ->
                        FahrerItem(fahrer = fahrer)
                    }
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
                contentDescription = if (selectedTab == 0) "Fahrzeug hinzufügen" else "Fahrer hinzufügen"
            )
        }
    }
}