package com.parkhub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.parkhub.app.model.tabs
import com.parkhub.app.ui.components.suche.*
import com.parkhub.app.ui.theme.*
import org.osmdroid.util.GeoPoint

val fahrzeugTypen = listOf(
    "Mercedes Sprinter", "VW Crafter",
    "Ford Transit", "Iveco Daily", "MAN TGE"
)

data class Sortierung(val label: String, val aufsteigend: Boolean)

val sortierOptionen = listOf(
    Sortierung("Preis aufsteigend", true),
    Sortierung("Preis absteigend", false),
    Sortierung("Entfernung aufsteigend", true),
    Sortierung("Entfernung absteigend", false)
)

data class StellplatzVorschau(
    val id: Int,
    val name: String,
    val entfernung: Int,
    val preisProStunde: Double,
    val bewertung: Float,
    val anzahlBewertungen: Int
) {
    val entfernungText: String
        get() = if (entfernung >= 1000)
            "${String.format("%.1f", entfernung / 1000.0)} km"
        else "$entfernung m"
}

val stellplatzVorschauListe = listOf(
    StellplatzVorschau(1, "Hauptstraße 18", 350, 3.40, 4.8f, 38),
    StellplatzVorschau(2, "Kaiserstraße 142", 520, 4.20, 4.5f, 21),
    StellplatzVorschau(3, "Sophienstraße 25", 780, 2.80, 4.2f, 15),
    StellplatzVorschau(4, "Yorckstraße 33", 1100, 3.80, 4.6f, 29),
    StellplatzVorschau(5, "Erbprinzenstraße 7", 1400, 5.20, 4.9f, 44),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SucheScreen() {
    var ort by remember { mutableStateOf("") }
    var ortLat by remember { mutableStateOf(49.0069) }
    var ortLng by remember { mutableStateOf(8.4037) }
    var datum by remember { mutableStateOf("") }
    var uhrzeitStart by remember { mutableStateOf("") }
    var uhrzeitEnd by remember { mutableStateOf("") }
    var selectedView by remember { mutableStateOf(0) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedFahrzeugTyp by remember { mutableStateOf("Mercedes Sprinter") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePickerStart by remember { mutableStateOf(false) }
    var showTimePickerEnd by remember { mutableStateOf(false) }
    var showSortieren by remember { mutableStateOf(false) }
    var selectedSortierung by remember { mutableStateOf(sortierOptionen[0]) }
    var showFehler by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val sortierteListe by remember(selectedSortierung) {
        derivedStateOf {
            when (selectedSortierung.label) {
                "Preis aufsteigend" -> stellplatzVorschauListe.sortedBy { it.preisProStunde }
                "Preis absteigend" -> stellplatzVorschauListe.sortedByDescending { it.preisProStunde }
                "Entfernung aufsteigend" -> stellplatzVorschauListe.sortedBy { it.entfernung }
                "Entfernung absteigend" -> stellplatzVorschauListe.sortedByDescending { it.entfernung }
                else -> stellplatzVorschauListe
            }
        }
    }

    val datePickerState = rememberDatePickerState()
    val timePickerStateStart = rememberTimePickerState(9, 0, is24Hour = true)
    val timePickerStateEnd = rememberTimePickerState(11, 0, is24Hour = true)

    val markers = listOf(
        Pair(GeoPoint(49.0069, 8.4037), "3,40 €"),
        Pair(GeoPoint(49.0089, 8.4010), "4,20 €"),
        Pair(GeoPoint(49.0050, 8.4060), "2,80 €")
    )

    // Snackbar Fehler anzeigen
    LaunchedEffect(showFehler) {
        if (showFehler) {
            snackbarHostState.showSnackbar(
                message = "Die Startzeit muss vor der Endzeit liegen.",
                duration = SnackbarDuration.Short
            )
            showFehler = false
        }
    }

    // Dialoge
    SucheDatePickerDialog(
        show = showDatePicker,
        state = datePickerState,
        onDismiss = { showDatePicker = false },
        onConfirm = { millis ->
            val sdf = java.text.SimpleDateFormat("d. MMM", java.util.Locale.GERMAN)
            datum = sdf.format(java.util.Date(millis))
            showDatePicker = false
        }
    )
    SucheTimePickerDialog(
        show = showTimePickerStart,
        state = timePickerStateStart,
        title = "Startzeit wählen",
        onDismiss = { showTimePickerStart = false },
        onConfirm = {
            val startMinuten = timePickerStateStart.hour * 60 + timePickerStateStart.minute
            val endMinuten = timePickerStateEnd.hour * 60 + timePickerStateEnd.minute
            if (uhrzeitEnd.isEmpty() || startMinuten < endMinuten) {
                uhrzeitStart = "%02d:%02d".format(
                    timePickerStateStart.hour,
                    timePickerStateStart.minute
                )
                showTimePickerStart = false
            } else {
                showTimePickerStart = false
                showFehler = true
            }
        }
    )
    SucheTimePickerDialog(
        show = showTimePickerEnd,
        state = timePickerStateEnd,
        title = "Endzeit wählen",
        onDismiss = { showTimePickerEnd = false },
        onConfirm = {
            val startMinuten = timePickerStateStart.hour * 60 + timePickerStateStart.minute
            val endMinuten = timePickerStateEnd.hour * 60 + timePickerStateEnd.minute
            if (uhrzeitStart.isEmpty() || endMinuten > startMinuten) {
                uhrzeitEnd = "%02d:%02d".format(
                    timePickerStateEnd.hour,
                    timePickerStateEnd.minute
                )
                showTimePickerEnd = false
            } else {
                showTimePickerEnd = false
                showFehler = true
            }
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { SucheTitelRow() }

            item {
                OrtSucheField(
                    ort = ort,
                    onOrtChange = { ort = it },
                    onOrtSelected = { _, lat, lng ->
                        ortLat = lat
                        ortLng = lng
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SucheDatumZeitSection(
                    datum = datum,
                    onDatumClick = { showDatePicker = true },
                    uhrzeitStart = uhrzeitStart,
                    onUhrzeitStartClick = { showTimePickerStart = true },
                    uhrzeitEnd = uhrzeitEnd,
                    onUhrzeitEndClick = { showTimePickerEnd = true }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SucheFahrzeugDropdown(
                    selectedFahrzeugTyp = selectedFahrzeugTyp,
                    dropdownExpanded = dropdownExpanded,
                    onDropdownChange = { dropdownExpanded = it },
                    onFahrzeugTypSelected = {
                        selectedFahrzeugTyp = it
                        dropdownExpanded = false
                    },
                    fahrzeugTypen = fahrzeugTypen
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                SucheTabRow(
                    tabs = tabs,
                    selectedView = selectedView,
                    onViewSelected = { selectedView = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                SucheErgebnisHeader(
                    anzahl = sortierteListe.size,
                    showSortieren = showSortieren,
                    onSortierenClick = { showSortieren = true },
                    onSortierenDismiss = { showSortieren = false },
                    selectedSortierung = selectedSortierung,
                    onSortierungSelected = {
                        selectedSortierung = it
                        showSortieren = false
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (selectedView == 0) {
                item {
                    OsmMapView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        latitude = ortLat,
                        longitude = ortLng,
                        markers = markers
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(items = sortierteListe, key = { it.id }) { stellplatz ->
                StellplatzListeItem(
                    stellplatz = stellplatz,
                    onClick = { }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}