package com.parkhub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkhub.app.data.AppDatabase
import com.parkhub.app.model.PillTab
import com.parkhub.app.ui.components.PillTabRow
import com.parkhub.app.ui.components.suche.*
import org.osmdroid.util.GeoPoint

data class Sortierung(val label: String, val aufsteigend: Boolean)

val sortierOptionen = listOf(
    Sortierung("Preis aufsteigend", true),
    Sortierung("Preis absteigend", false),
    Sortierung("Entfernung aufsteigend", true),
    Sortierung("Entfernung absteigend", false)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SucheScreen(
    viewModel: SucheViewModel = viewModel(
        factory = SucheViewModelFactory(
            stellplatzDao = AppDatabase.getDatabase(LocalContext.current).stellplatzDao(),
            adresseDao = AppDatabase.getDatabase(LocalContext.current).adresseDao(),
            bewertungDao = AppDatabase.getDatabase(LocalContext.current).bewertungDao(),
            fahrzeugTypDao = AppDatabase.getDatabase(LocalContext.current).fahrzeugTypDao()
        )
    )
) {
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

    val stellplaetzeListe by viewModel.stellplaetzeMitEntfernung(ortLat, ortLng)
        .collectAsState(initial = emptyList())

    val sortierteListe by remember(selectedSortierung, stellplaetzeListe) {
        derivedStateOf {
            when (selectedSortierung.label) {
                "Preis aufsteigend" -> stellplaetzeListe.sortedBy { it.stellplatz.preis_stunde }
                "Preis absteigend" -> stellplaetzeListe.sortedByDescending { it.stellplatz.preis_stunde }
                "Entfernung aufsteigend" -> stellplaetzeListe.sortedBy { it.entfernungMeter }
                "Entfernung absteigend" -> stellplaetzeListe.sortedByDescending { it.entfernungMeter }
                else -> stellplaetzeListe
            }
        }
    }
    val fahrzeugTypenListe by viewModel.fahrzeugTypListeFlow.collectAsState(initial = emptyList())

    val datePickerState = rememberDatePickerState()
    var startHour by remember { mutableStateOf(9) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(11) }
    var endMinute by remember { mutableStateOf(0) }

    val markers = sortierteListe.map { details ->
        Pair(
            GeoPoint(details.stellplatz.gps_lat.toDouble(), details.stellplatz.gps_lng.toDouble()),
            "${details.stellplatz.preis_stunde} €"
        )
    }

    val tabs = listOf(
        PillTab("Karte", Icons.Outlined.Map),
        PillTab("Liste", Icons.AutoMirrored.Outlined.FormatListBulleted)
    )

    LaunchedEffect(showFehler) {
        if (showFehler) {
            snackbarHostState.showSnackbar(
                message = "Die Startzeit muss vor der Endzeit liegen.",
                duration = SnackbarDuration.Short
            )
            showFehler = false
        }
    }

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
        title = "Startzeit wählen",
        selectedHour = startHour,
        selectedMinute = startMinute,
        onHourSelected = { startHour = it },
        onMinuteSelected = { startMinute = it },
        onDismiss = { showTimePickerStart = false },
        onConfirm = {
            val startMinuten = startHour * 60 + startMinute
            val endMinuten = endHour * 60 + endMinute
            if (uhrzeitEnd.isEmpty() || startMinuten < endMinuten) {
                uhrzeitStart = "%02d:%02d".format(startHour, startMinute)
                showTimePickerStart = false
            } else {
                showTimePickerStart = false
                showFehler = true
            }
        }
    )

    SucheTimePickerDialog(
        show = showTimePickerEnd,
        title = "Endzeit wählen",
        selectedHour = endHour,
        selectedMinute = endMinute,
        onHourSelected = { endHour = it },
        onMinuteSelected = { endMinute = it },
        onDismiss = { showTimePickerEnd = false },
        onConfirm = {
            val startMinuten = startHour * 60 + startMinute
            val endMinuten = endHour * 60 + endMinute
            if (uhrzeitStart.isEmpty() || endMinuten > startMinuten) {
                uhrzeitEnd = "%02d:%02d".format(endHour, endMinute)
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
                    fahrzeugTypen = fahrzeugTypenListe.map { it.bezeichnung }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                PillTabRow(
                    tabs = tabs,
                    selectedTab = selectedView,
                    onTabSelected = { selectedView = it },
                    modifier = Modifier.fillMaxWidth()
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

            items(items = sortierteListe, key = { it.stellplatz.id }) { details ->
                StellplatzListeItem(
                    stellplatz = details,
                    onClick = { }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}