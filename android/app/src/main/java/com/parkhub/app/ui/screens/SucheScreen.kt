package com.parkhub.app.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.parkhub.app.data.AppDatabase
import com.parkhub.app.model.PillTab
import com.parkhub.app.ui.components.PillTabRow
import com.parkhub.app.ui.components.suche.*
import com.parkhub.app.ui.theme.Gray
import org.osmdroid.util.GeoPoint
import java.util.Calendar

data class Sortierung(val label: String, val aufsteigend: Boolean)

val sortierOptionen = listOf(
    Sortierung("Preis aufsteigend", true),
    Sortierung("Preis absteigend", false),
    Sortierung("Entfernung aufsteigend", true),
    Sortierung("Entfernung absteigend", false)
)

private fun aufMitternachtSetzen(millis: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    var ortLat by remember { mutableStateOf<Double?>(null) }
    var ortLng by remember { mutableStateOf<Double?>(null) }
    var datum by remember {
        mutableStateOf(
            java.text.SimpleDateFormat("d. MMM", java.util.Locale.GERMAN)
                .format(java.util.Date())
        )
    }
    var datumMillis by remember { mutableStateOf<Long?>(aufMitternachtSetzen(System.currentTimeMillis())) }

    val jetzt = Calendar.getInstance()
    val aktuelleMinute = jetzt.get(Calendar.MINUTE)
    if (aktuelleMinute > 0) {
        jetzt.add(Calendar.HOUR_OF_DAY, 1)
    }
    jetzt.set(Calendar.MINUTE, 0)
    jetzt.set(Calendar.SECOND, 0)
    jetzt.set(Calendar.MILLISECOND, 0)
    val aufgerundeteStunde = jetzt.get(Calendar.HOUR_OF_DAY)

    var startHour by remember { mutableStateOf(aufgerundeteStunde) }
    var startMinute by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf((aufgerundeteStunde + 1) % 24) }
    var endMinute by remember { mutableStateOf(0) }

    var uhrzeitStart by remember {
        mutableStateOf("%02d:%02d".format(aufgerundeteStunde, 0))
    }
    var uhrzeitEnd by remember {
        mutableStateOf("%02d:%02d".format((aufgerundeteStunde + 1) % 24, 0))
    }

    var selectedView by remember { mutableStateOf(0) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedFahrzeugTyp by remember { mutableStateOf("Mercedes Sprinter") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePickerStart by remember { mutableStateOf(false) }
    var showTimePickerEnd by remember { mutableStateOf(false) }
    var showSortieren by remember { mutableStateOf(false) }
    var selectedSortierung by remember { mutableStateOf(sortierOptionen[0]) }
    var showFehler by remember { mutableStateOf(false) }
    var showFehlerMessage by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val aktuellerFilter by viewModel.filter.collectAsState()

    val filterIstStandard = remember(aktuellerFilter) {
        aktuellerFilter.minLaenge == 300f &&
                aktuellerFilter.minBreite == 180f &&
                aktuellerFilter.minHoehe == 180f &&
                aktuellerFilter.minPreis == 2.0f &&
                aktuellerFilter.maxPreis == 8.0f &&
                aktuellerFilter.minBewertung == 0f
    }

    val sucheVollstaendig = ortLat != null && ortLng != null &&
            datumMillis != null && uhrzeitStart.isNotEmpty() && uhrzeitEnd.isNotEmpty()

    val suchVon = remember(datumMillis, startHour, startMinute) {
        datumMillis?.let { basis ->
            Calendar.getInstance().apply {
                timeInMillis = basis
                set(Calendar.HOUR_OF_DAY, startHour)
                set(Calendar.MINUTE, startMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
    val suchBis = remember(datumMillis, endHour, endMinute) {
        datumMillis?.let { basis ->
            Calendar.getInstance().apply {
                timeInMillis = basis
                set(Calendar.HOUR_OF_DAY, endHour)
                set(Calendar.MINUTE, endMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }

    // fahrzeugTypenListe wird jetzt VOR dem LaunchedEffect deklariert,
    // da der Effect davon abhängt (besser lesbar, gleiche Funktion wie vorher).
    val fahrzeugTypenListe by viewModel.fahrzeugTypListeFlow.collectAsState(initial = emptyList())

    LaunchedEffect(sucheVollstaendig, suchVon, suchBis, ortLat, ortLng, selectedFahrzeugTyp, fahrzeugTypenListe) {
        if (sucheVollstaendig && suchVon != null && suchBis != null) {
            // Fix: "sort" existierte nicht, korrekt ist fahrzeugTypenListe.
            val gewaehlterTyp = fahrzeugTypenListe.find { it.bezeichnung == selectedFahrzeugTyp }
            viewModel.updateFilter(
                viewModel.filter.value.copy(
                    von = suchVon,
                    bis = suchBis,
                    fahrzeugTyp = gewaehlterTyp
                )
            )
        }
    }

    val stellplaetzeListe by if (sucheVollstaendig && ortLat != null && ortLng != null) {
        viewModel.stellplaetzeMitEntfernung(ortLat!!, ortLng!!)
            .collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    val sortierteListe by remember(selectedSortierung, stellplaetzeListe) {
        derivedStateOf {
            when (selectedSortierung.label) {
                "Preis aufsteigend", "Preis absteigend" -> stellplaetzeListe
                "Entfernung aufsteigend" -> stellplaetzeListe.sortedBy { it.entfernungMeter }
                "Entfernung absteigend" -> stellplaetzeListe.sortedByDescending { it.entfernungMeter }
                else -> stellplaetzeListe
            }
        }
    }

    val markers = sortierteListe.map { details ->
        Pair(
            GeoPoint(details.stellplatz.gps_lat.toDouble(), details.stellplatz.gps_lng.toDouble()),
            "${details.adresse?.strasse} ${details.adresse?.hausnummer}, ${details.adresse?.ort} - ${details.stellplatz.preis_stunde} €"
        )
    }

    val tabs = listOf(
        PillTab("Karte", Icons.Outlined.Map),
        PillTab("Liste", Icons.AutoMirrored.Outlined.FormatListBulleted)
    )

    LaunchedEffect(showFehler) {
        if (showFehler && showFehlerMessage != "") {
            snackbarHostState.showSnackbar(
                message = showFehlerMessage,
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
            val heuteMitternacht = aufMitternachtSetzen(System.currentTimeMillis())
            val gewaehltesDatum = aufMitternachtSetzen(millis)

            if (gewaehltesDatum < heuteMitternacht) {
                showDatePicker = false
                showFehler = true
                showFehlerMessage = "Das gewählte Datum muss heute oder in der Zukunft liegen."
            } else {
                val sdf = java.text.SimpleDateFormat("d. MMM", java.util.Locale.GERMAN)
                datum = sdf.format(java.util.Date(millis))
                datumMillis = gewaehltesDatum
                showDatePicker = false
            }
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

            // Prüfen ob das gewählte Datum heute ist - nur dann ist eine
            // Vergangenheits-Prüfung für die Uhrzeit überhaupt nötig.
            val istHeute = datumMillis != null &&
                    aufMitternachtSetzen(datumMillis!!) == aufMitternachtSetzen(System.currentTimeMillis())

            val jetztMinuten = run {
                val cal = Calendar.getInstance()
                cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
            }

            if (istHeute && startMinuten < jetztMinuten) {
                showTimePickerStart = false
                showFehler = true
                showFehlerMessage = "Die Startzeit darf nicht in der Vergangenheit liegen."
            } else if (uhrzeitEnd.isEmpty() || startMinuten < endMinuten) {
                uhrzeitStart = "%02d:%02d".format(startHour, startMinute)
                showTimePickerStart = false
            } else {
                showTimePickerStart = false
                showFehler = true
                showFehlerMessage = "Die Startzeit muss vor der Endzeit liegen."
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

            val istHeute = datumMillis != null &&
                    aufMitternachtSetzen(datumMillis!!) == aufMitternachtSetzen(System.currentTimeMillis())

            val jetztMinuten = run {
                val cal = Calendar.getInstance()
                cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
            }

            if (istHeute && endMinuten < jetztMinuten) {
                showTimePickerEnd = false
                showFehler = true
                showFehlerMessage = "Die Endzeit darf nicht in der Vergangenheit liegen."
            } else if (uhrzeitStart.isEmpty() || endMinuten > startMinuten) {
                uhrzeitEnd = "%02d:%02d".format(endHour, endMinute)
                showTimePickerEnd = false
            } else {
                showTimePickerEnd = false
                showFehler = true
                showFehlerMessage = "Die Endzeit muss nach der Startzeit liegen."
            }
        }
    )

    StellplatzFilterDialog(
        show = showFilterDialog,
        filter = aktuellerFilter,
        ergebnisAnzahl = sortierteListe.size,
        onFilterChange = { neuerFilter -> viewModel.updateFilter(neuerFilter) },
        onDismiss = { showFilterDialog = false }
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {  _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 8.dp)
                ) {
                    SucheTitelRow(
                        onFilterClick = { showFilterDialog = true },
                        filterAktiv = !filterIstStandard
                    )
                }
            }

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

            if (!sucheVollstaendig) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Bitte Ort, Datum und Zeit auswählen",
                            fontSize = 14.sp,
                            color = Gray
                        )
                    }
                }
            } else {
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
                            if (it.label == "Preis aufsteigend" || it.label == "Preis absteigend") {
                                viewModel.updateFilter(
                                    viewModel.filter.value.copy(preisAufsteigend = it.label == "Preis aufsteigend")
                                )
                            }
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
                            latitude = ortLat ?: 49.0069,
                            longitude = ortLng ?: 8.4037,
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
}