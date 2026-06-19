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

/**
 * Haupt-Screen für die Stellplatzsuche.
 *
 * Der gesamte lokale UI-Zustand (Ort, Datum, Uhrzeiten, Ansicht,
 * Dialog-Sichtbarkeiten) steckt in [SucheUiState] (siehe
 * SucheUiState.kt). Diese Datei kümmert sich nur noch um:
 *  - das Verbinden des States mit dem [SucheViewModel] (Filter-Updates,
 *    reaktives Laden der Stellplätze)
 *  - die drei Auswahl-Dialoge (Datum, Start-/Endzeit, Filter)
 *  - das eigentliche Layout (Sticky Header, Eingabefelder, Tabs,
 *    Karte/Liste)
 */
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
    val state = rememberSucheUiState()
    var ausgewaehlterStellplatz by remember { mutableStateOf<StellplatzMitDetails?>(null) }
    var zeigeBuchungsBestaetigung by remember { mutableStateOf(false) }

    if (zeigeBuchungsBestaetigung && ausgewaehlterStellplatz != null) {
        BuchungsBestaetigungScreen(
            stellplatz = ausgewaehlterStellplatz!!,
            von = state.suchVon,
            bis = state.suchBis,
            fahrzeug = null,
            fahrer = null,
            onBackClick = { zeigeBuchungsBestaetigung = false },
            onJetztBuchenClick = {
                zeigeBuchungsBestaetigung = false
                ausgewaehlterStellplatz = null
            }
        )
        return
    }

    ausgewaehlterStellplatz?.let { details ->
        StellplatzDetailScreen(
            stellplatz = details,
            von = state.suchVon,
            bis = state.suchBis,
            onBackClick = { ausgewaehlterStellplatz = null },
            onBuchenClick = {
                zeigeBuchungsBestaetigung = true
            }
        )
        return
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val snackbarHostState = remember { SnackbarHostState() }

    // Aktueller Filter-Zustand kommt reaktiv aus dem ViewModel. Jede
    // Änderung hier löst automatisch eine neue, gefilterte DB-Query aus.
    val aktuellerFilter by viewModel.filter.collectAsState()

    // Prüft, ob der Filter noch auf den Werkseinstellungen steht. Wird
    // genutzt, um das Filter-Icon und einen kleinen Punkt grün
    // einzufärben, sobald irgendein Wert vom Standard abweicht.
    val filterIstStandard = remember(aktuellerFilter) {
        aktuellerFilter.minLaenge == 300f &&
                aktuellerFilter.minBreite == 180f &&
                aktuellerFilter.minHoehe == 180f &&
                aktuellerFilter.minPreis == 2.0f &&
                aktuellerFilter.maxPreis == 8.0f &&
                aktuellerFilter.minBewertung == 0f
    }

    val fahrzeugTypenListe by viewModel.fahrzeugTypListeFlow.collectAsState(initial = emptyList())

    // Sobald sich Suchzeitraum, Ort oder Fahrzeugtyp ändern UND die
    // Suche vollständig ist, wird der Filter im ViewModel mit den
    // neuen Werten aktualisiert. Das löst automatisch eine neue,
    // reaktive Datenbank-Abfrage über den Flow im ViewModel aus.
    val suchVon = state.suchVon
    val suchBis = state.suchBis
    LaunchedEffect(
        state.sucheVollstaendig, suchVon, suchBis,
        state.ortLat, state.ortLng, state.selectedFahrzeugTyp, fahrzeugTypenListe
    ) {
        if (state.sucheVollstaendig && suchVon != null && suchBis != null) {
            val gewaehlterTyp = fahrzeugTypenListe.find { it.bezeichnung == state.selectedFahrzeugTyp }
            viewModel.updateFilter(
                viewModel.filter.value.copy(
                    von = suchVon,
                    bis = suchBis,
                    fahrzeugTyp = gewaehlterTyp
                )
            )
        }
    }

    // Solange die Suche nicht vollständig ist, bleibt die Liste leer
    // und es wird gar nicht erst beim ViewModel nachgefragt - das
    // verhindert unnötige Datenbankzugriffe mit unvollständigen Daten.
    val ortLat = state.ortLat
    val ortLng = state.ortLng
    val stellplaetzeListe by if (state.sucheVollstaendig && ortLat != null && ortLng != null) {
        viewModel.stellplaetzeMitEntfernung(ortLat, ortLng)
            .collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    // Preis-Sortierung kommt bereits sortiert aus der DB-Query
    // (ORDER BY mit CASE WHEN), daher hier nur durchreichen. Die
    // Entfernung wird dagegen lokal sortiert, weil die Distanz erst
    // zur Laufzeit per Haversine-Formel berechnet wird und SQLite das
    // nicht nativ kann.
    val sortierteListe by remember(state.selectedSortierung, stellplaetzeListe) {
        derivedStateOf {
            when (state.selectedSortierung.label) {
                "Preis aufsteigend", "Preis absteigend" -> stellplaetzeListe
                "Entfernung aufsteigend" -> stellplaetzeListe.sortedBy { it.entfernungMeter }
                "Entfernung absteigend" -> stellplaetzeListe.sortedByDescending { it.entfernungMeter }
                else -> stellplaetzeListe
            }
        }
    }

    // Marker für die Kartenansicht: Position plus Anzeigetext
    // (vollständige Adresse und Preis pro Stunde).
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

    // Zeigt die Fehler-Snackbar an, sobald state.showFehler auf true
    // gesetzt wird. Läuft als Coroutine, da showSnackbar() eine
    // suspend-Funktion ist und nicht direkt im Composable aufrufbar wäre.
    LaunchedEffect(state.showFehler) {
        if (state.showFehler && state.showFehlerMessage != "") {
            snackbarHostState.showSnackbar(
                message = state.showFehlerMessage,
                duration = SnackbarDuration.Short
            )
            state.showFehler = false
        }
    }

    // Die drei Auswahl-Dialoge mit ihrer Validierungslogik
    // (Vergangenheits-Prüfung, Start-vor-Ende-Prüfung) leben in
    // SucheValidierung.kt im components-Package.
    SucheDatumDialog(
        show = state.showDatePicker,
        state = datePickerState,
        onDismiss = { state.showDatePicker = false },
        onDatumGewaehlt = { neuerText, neuerMillis ->
            state.datum = neuerText
            state.datumMillis = neuerMillis
        },
        onFehler = { nachricht -> state.zeigeFehler(nachricht) }
    )

    SucheStartzeitDialog(
        show = state.showTimePickerStart,
        startHour = state.startHour,
        startMinute = state.startMinute,
        endHour = state.endHour,
        endMinute = state.endMinute,
        uhrzeitEndGesetzt = state.uhrzeitEnd.isNotEmpty(),
        datumMillis = state.datumMillis,
        onHourSelected = { state.startHour = it },
        onMinuteSelected = { state.startMinute = it },
        onDismiss = { state.showTimePickerStart = false },
        onUebernehmen = { neuerText ->
            state.uhrzeitStart = neuerText
            state.showTimePickerStart = false
        },
        onFehler = { nachricht ->
            state.showTimePickerStart = false
            state.zeigeFehler(nachricht)
        }
    )

    SucheEndzeitDialog(
        show = state.showTimePickerEnd,
        startHour = state.startHour,
        startMinute = state.startMinute,
        endHour = state.endHour,
        endMinute = state.endMinute,
        uhrzeitStartGesetzt = state.uhrzeitStart.isNotEmpty(),
        datumMillis = state.datumMillis,
        onHourSelected = { state.endHour = it },
        onMinuteSelected = { state.endMinute = it },
        onDismiss = { state.showTimePickerEnd = false },
        onUebernehmen = { neuerText ->
            state.uhrzeitEnd = neuerText
            state.showTimePickerEnd = false
        },
        onFehler = { nachricht ->
            state.showTimePickerEnd = false
            state.zeigeFehler(nachricht)
        }
    )

    StellplatzFilterDialog(
        show = state.showFilterDialog,
        filter = aktuellerFilter,
        ergebnisAnzahl = sortierteListe.size,
        onFilterChange = { neuerFilter -> viewModel.updateFilter(neuerFilter) },
        onDismiss = { state.showFilterDialog = false }
    )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            // Sticky Header: bleibt beim Scrollen oben fixiert, damit
            // der Filter-Zugriff immer erreichbar bleibt. Eigener
            // Hintergrund nötig, sonst scheint der Inhalt darunter durch.
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 8.dp)
                ) {
                    SucheTitelRow(
                        onFilterClick = { state.showFilterDialog = true },
                        filterAktiv = !filterIstStandard
                    )
                }
            }

            item {
                OrtSucheField(
                    ort = state.ort,
                    onOrtChange = { state.ort = it },
                    onOrtSelected = { _, lat, lng ->
                        state.ortLat = lat
                        state.ortLng = lng
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SucheDatumZeitSection(
                    datum = state.datum,
                    onDatumClick = { state.showDatePicker = true },
                    uhrzeitStart = state.uhrzeitStart,
                    onUhrzeitStartClick = { state.showTimePickerStart = true },
                    uhrzeitEnd = state.uhrzeitEnd,
                    onUhrzeitEndClick = { state.showTimePickerEnd = true }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                SucheFahrzeugDropdown(
                    selectedFahrzeugTyp = state.selectedFahrzeugTyp,
                    dropdownExpanded = state.dropdownExpanded,
                    onDropdownChange = { state.dropdownExpanded = it },
                    onFahrzeugTypSelected = {
                        state.selectedFahrzeugTyp = it
                        state.dropdownExpanded = false
                    },
                    fahrzeugTypen = fahrzeugTypenListe.map { it.bezeichnung }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Solange nicht alle Pflichtfelder gesetzt sind: nur
            // Hinweistext, keine Tabs/Karte/Liste anzeigen.
            if (!state.sucheVollstaendig) {
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
                        selectedTab = state.selectedView,
                        onTabSelected = { state.selectedView = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    SucheErgebnisHeader(
                        anzahl = sortierteListe.size,
                        showSortieren = state.showSortieren,
                        onSortierenClick = { state.showSortieren = true },
                        onSortierenDismiss = { state.showSortieren = false },
                        selectedSortierung = state.selectedSortierung,
                        onSortierungSelected = {
                            state.selectedSortierung = it
                            state.showSortieren = false
                            // Bei Preis-Sortierung muss zusätzlich das
                            // ViewModel informiert werden, damit die
                            // DB-Query mit dem richtigen ORDER BY neu läuft.
                            if (it.label == "Preis aufsteigend" || it.label == "Preis absteigend") {
                                viewModel.updateFilter(
                                    viewModel.filter.value.copy(preisAufsteigend = it.label == "Preis aufsteigend")
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Karte nur anzeigen, wenn der Karten-Tab aktiv ist
                if (state.selectedView == 0) {
                    item {
                        OsmMapView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            latitude = state.ortLat ?: 49.0069,
                            longitude = state.ortLng ?: 8.4037,
                            markers = markers
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                items(items = sortierteListe, key = { it.stellplatz.id }) { details ->
                    StellplatzListeItem(
                        stellplatz = details,
                        onClick = { ausgewaehlterStellplatz = details }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}