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

/**
 * Haupt-Screen für die Stellplatzsuche.
 *
 * Funktionsweise: Der Nutzer wählt einen Ort, ein Datum, einen
 * Zeitraum und optional einen Fahrzeugtyp. Erst wenn alle drei
 * Pflichtangaben (Ort, Datum, Zeitraum) vollständig sind
 * ([sucheVollstaendig]), werden sie zu einem konkreten Suchzeitraum
 * kombiniert und an das [SucheViewModel] weitergereicht. Das ViewModel
 * lädt daraufhin reaktiv passende Stellplätze aus der Datenbank,
 * inklusive Entfernungsberechnung zum gewählten Ort.
 *
 * Die Ergebnisse lassen sich zwischen Karten- und Listenansicht
 * umschalten, nach Preis oder Entfernung sortieren, und über ein
 * Filter-BottomSheet weiter einschränken (Mindestmaße, Preisspanne,
 * Mindestbewertung).
 *
 * Validierung von Datum und Uhrzeit (keine Werte in der Vergangenheit,
 * Start vor Ende) ist in eigene Dialog-Komponenten in
 * [com.parkhub.app.ui.components.suche] ausgelagert, siehe
 * SucheDatumDialog, SucheStartzeitDialog und SucheEndzeitDialog.
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
    // ===== ORT =====
    // ortLat/ortLng bleiben null, bis der Nutzer aktiv einen Ort
    // ausgewählt hat (manuell aus der Vorschlagsliste oder per
    // automatischer Standorterkennung in OrtSucheField).
    var ort by remember { mutableStateOf("") }
    var ortLat by remember { mutableStateOf<Double?>(null) }
    var ortLng by remember { mutableStateOf<Double?>(null) }

    // ===== DATUM =====
    // Standardmäßig auf "heute" vorausgefüllt: datum ist der
    // formatierte Anzeigetext, datumMillis der auf Mitternacht
    // normalisierte Timestamp für Berechnungen.
    var datum by remember {
        mutableStateOf(
            java.text.SimpleDateFormat("d. MMM", java.util.Locale.GERMAN)
                .format(java.util.Date())
        )
    }
    var datumMillis by remember { mutableStateOf<Long?>(aufMitternachtSetzen(System.currentTimeMillis())) }

    // ===== STANDARD-UHRZEIT BERECHNEN =====
    // Die Startzeit wird auf die nächste volle Stunde aufgerundet,
    // z. B. 14:23 Uhr -> Start 15:00 Uhr. Steht die Uhr bereits exakt
    // auf einer vollen Stunde, bleibt sie unverändert. Die Endzeit ist
    // standardmäßig eine Stunde später als die Startzeit.
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
    // % 24 verhindert eine ungültige Stunde 24, falls aufgerundeteStunde = 23 ist
    var endHour by remember { mutableStateOf((aufgerundeteStunde + 1) % 24) }
    var endMinute by remember { mutableStateOf(0) }

    var uhrzeitStart by remember {
        mutableStateOf("%02d:%02d".format(aufgerundeteStunde, 0))
    }
    var uhrzeitEnd by remember {
        mutableStateOf("%02d:%02d".format((aufgerundeteStunde + 1) % 24, 0))
    }

    // ===== SONSTIGER UI-ZUSTAND =====
    var selectedView by remember { mutableStateOf(0) } // 0 = Karte, 1 = Liste
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

    // Die Suche gilt erst als vollständig, wenn Ort, Datum und beide
    // Uhrzeiten gesetzt sind. Solange das nicht zutrifft, zeigt der
    // Screen nur einen Hinweistext statt Tabs, Karte oder Liste.
    val sucheVollstaendig = ortLat != null && ortLng != null &&
            datumMillis != null && uhrzeitStart.isNotEmpty() && uhrzeitEnd.isNotEmpty()

    // Kombiniert das gewählte Datum mit der Startzeit zu einem
    // einzigen Timestamp, den die Datenbank-Query als Suchbeginn nutzt.
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
    // Kombiniert das gewählte Datum mit der Endzeit, analog zu suchVon.
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

    val fahrzeugTypenListe by viewModel.fahrzeugTypListeFlow.collectAsState(initial = emptyList())

    // Sobald sich Suchzeitraum, Ort oder Fahrzeugtyp ändern UND die
    // Suche vollständig ist, wird der Filter im ViewModel mit den
    // neuen Werten aktualisiert. Das löst automatisch eine neue,
    // reaktive Datenbank-Abfrage über den Flow im ViewModel aus.
    LaunchedEffect(sucheVollstaendig, suchVon, suchBis, ortLat, ortLng, selectedFahrzeugTyp, fahrzeugTypenListe) {
        if (sucheVollstaendig && suchVon != null && suchBis != null) {
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

    // Solange die Suche nicht vollständig ist, bleibt die Liste leer
    // und es wird gar nicht erst beim ViewModel nachgefragt - das
    // verhindert unnötige Datenbankzugriffe mit unvollständigen Daten.
    val stellplaetzeListe by if (sucheVollstaendig && ortLat != null && ortLng != null) {
        viewModel.stellplaetzeMitEntfernung(ortLat!!, ortLng!!)
            .collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    // Preis-Sortierung kommt bereits sortiert aus der DB-Query
    // (ORDER BY mit CASE WHEN), daher hier nur durchreichen. Die
    // Entfernung wird dagegen lokal sortiert, weil die Distanz erst
    // zur Laufzeit per Haversine-Formel berechnet wird und SQLite das
    // nicht nativ kann.
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

    // Zeigt die Fehler-Snackbar an, sobald showFehler auf true
    // gesetzt wird. Läuft als Coroutine, da showSnackbar() eine
    // suspend-Funktion ist und nicht direkt im Composable aufrufbar wäre.
    LaunchedEffect(showFehler) {
        if (showFehler && showFehlerMessage != "") {
            snackbarHostState.showSnackbar(
                message = showFehlerMessage,
                duration = SnackbarDuration.Short
            )
            showFehler = false
        }
    }

    // Die drei Auswahl-Dialoge mit ihrer Validierungslogik
    // (Vergangenheits-Prüfung, Start-vor-Ende-Prüfung) leben in
    // SucheValidierung.kt im components-Package.
    SucheDatumDialog(
        show = showDatePicker,
        state = datePickerState,
        onDismiss = { showDatePicker = false },
        onDatumGewaehlt = { neuerText, neuerMillis ->
            datum = neuerText
            datumMillis = neuerMillis
        },
        onFehler = { nachricht ->
            showFehler = true
            showFehlerMessage = nachricht
        }
    )

    SucheStartzeitDialog(
        show = showTimePickerStart,
        startHour = startHour,
        startMinute = startMinute,
        endHour = endHour,
        endMinute = endMinute,
        uhrzeitEndGesetzt = uhrzeitEnd.isNotEmpty(),
        datumMillis = datumMillis,
        onHourSelected = { startHour = it },
        onMinuteSelected = { startMinute = it },
        onDismiss = { showTimePickerStart = false },
        onUebernehmen = { neuerText ->
            uhrzeitStart = neuerText
            showTimePickerStart = false
        },
        onFehler = { nachricht ->
            showTimePickerStart = false
            showFehler = true
            showFehlerMessage = nachricht
        }
    )

    SucheEndzeitDialog(
        show = showTimePickerEnd,
        startHour = startHour,
        startMinute = startMinute,
        endHour = endHour,
        endMinute = endMinute,
        uhrzeitStartGesetzt = uhrzeitStart.isNotEmpty(),
        datumMillis = datumMillis,
        onHourSelected = { endHour = it },
        onMinuteSelected = { endMinute = it },
        onDismiss = { showTimePickerEnd = false },
        onUebernehmen = { neuerText ->
            uhrzeitEnd = neuerText
            showTimePickerEnd = false
        },
        onFehler = { nachricht ->
            showTimePickerEnd = false
            showFehler = true
            showFehlerMessage = nachricht
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

            // Solange nicht alle Pflichtfelder gesetzt sind: nur
            // Hinweistext, keine Tabs/Karte/Liste anzeigen.
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