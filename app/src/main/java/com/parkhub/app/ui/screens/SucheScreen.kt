package com.parkhub.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.components.suche.OrtSucheField
import com.parkhub.app.ui.components.suche.OsmMapView
import com.parkhub.app.ui.components.suche.StellplatzListeItem
import com.parkhub.app.ui.theme.*
import org.osmdroid.util.GeoPoint

val fahrzeugTypen = listOf(
    "Mercedes Sprinter",
    "VW Crafter",
    "Ford Transit",
    "Iveco Daily",
    "MAN TGE"
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

    // Sortierte Liste — wird neu berechnet wenn Sortierung sich ändert
    val sortierteListe by remember(selectedSortierung) {
        derivedStateOf {
            when (selectedSortierung.label) {
                "Preis aufsteigend" ->
                    stellplatzVorschauListe.sortedBy { it.preisProStunde }
                "Preis absteigend" ->
                    stellplatzVorschauListe.sortedByDescending { it.preisProStunde }
                "Entfernung aufsteigend" ->
                    stellplatzVorschauListe.sortedBy { it.entfernung }
                "Entfernung absteigend" ->
                    stellplatzVorschauListe.sortedByDescending { it.entfernung }
                else -> stellplatzVorschauListe
            }
        }
    }

    val datePickerState = rememberDatePickerState()
    val timePickerStateStart = rememberTimePickerState(
        initialHour = 9,
        initialMinute = 0,
        is24Hour = true
    )
    val timePickerStateEnd = rememberTimePickerState(
        initialHour = 11,
        initialMinute = 0,
        is24Hour = true
    )

    val tabs = listOf("Karte", "Liste")

    val markers = listOf(
        Pair(GeoPoint(49.0069, 8.4037), "3,40 €"),
        Pair(GeoPoint(49.0089, 8.4010), "4,20 €"),
        Pair(GeoPoint(49.0050, 8.4060), "2,80 €")
    )

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = java.text.SimpleDateFormat(
                            "d. MMM",
                            java.util.Locale.GERMAN
                        )
                        datum = sdf.format(java.util.Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = ParkHubGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Abbrechen", color = Gray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = ParkHubGreen,
                    todayDateBorderColor = ParkHubGreen
                )
            )
        }
    }

    // TimePicker Start
    if (showTimePickerStart) {
        AlertDialog(
            onDismissRequest = { showTimePickerStart = false },
            confirmButton = {
                TextButton(onClick = {
                    uhrzeitStart = "%02d:%02d".format(
                        timePickerStateStart.hour,
                        timePickerStateStart.minute
                    )
                    showTimePickerStart = false
                }) {
                    Text("OK", color = ParkHubGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerStart = false }) {
                    Text("Abbrechen", color = Gray)
                }
            },
            title = { Text("Startzeit wählen") },
            text = {
                TimePicker(
                    state = timePickerStateStart,
                    colors = TimePickerDefaults.colors(
                        selectorColor = ParkHubGreen,
                        timeSelectorSelectedContainerColor = ParkHubGreenContainer,
                        timeSelectorSelectedContentColor = ParkHubGreen
                    )
                )
            }
        )
    }

    // TimePicker Ende
    if (showTimePickerEnd) {
        AlertDialog(
            onDismissRequest = { showTimePickerEnd = false },
            confirmButton = {
                TextButton(onClick = {
                    uhrzeitEnd = "%02d:%02d".format(
                        timePickerStateEnd.hour,
                        timePickerStateEnd.minute
                    )
                    showTimePickerEnd = false
                }) {
                    Text("OK", color = ParkHubGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerEnd = false }) {
                    Text("Abbrechen", color = Gray)
                }
            },
            title = { Text("Endzeit wählen") },
            text = {
                TimePicker(
                    state = timePickerStateEnd,
                    colors = TimePickerDefaults.colors(
                        selectorColor = ParkHubGreen,
                        timeSelectorSelectedContainerColor = ParkHubGreenContainer,
                        timeSelectorSelectedContentColor = ParkHubGreen
                    )
                )
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stellplatz suchen",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.onBackground
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
            OutlinedTextField(
                value = datum,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                label = { Text("Datum") },
                placeholder = { Text("Datum wählen") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = ParkHubGreen
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ParkHubGreen,
                    focusedLabelColor = ParkHubGreen,
                    disabledBorderColor = Gray,
                    disabledLabelColor = Gray,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLeadingIconColor = ParkHubGreen
                ),
                enabled = false
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uhrzeitStart,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePickerStart = true },
                    label = { Text("Von") },
                    placeholder = { Text("09:00") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ParkHubGreen,
                        focusedLabelColor = ParkHubGreen,
                        disabledBorderColor = Gray,
                        disabledLabelColor = Gray,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = false
                )
                OutlinedTextField(
                    value = uhrzeitEnd,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePickerEnd = true },
                    label = { Text("Bis") },
                    placeholder = { Text("11:00") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ParkHubGreen,
                        focusedLabelColor = ParkHubGreen,
                        disabledBorderColor = Gray,
                        disabledLabelColor = Gray,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    enabled = false
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedFahrzeugTyp,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Fahrzeugtyp") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.LocalShipping,
                            contentDescription = null,
                            tint = ParkHubGreen
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ParkHubGreen,
                        focusedLabelColor = ParkHubGreen
                    )
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    fahrzeugTypen.forEach { typ ->
                        DropdownMenuItem(
                            text = { Text(typ) },
                            onClick = {
                                selectedFahrzeugTyp = typ
                                dropdownExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.LocalShipping,
                                    contentDescription = null,
                                    tint = if (selectedFahrzeugTyp == typ)
                                        ParkHubGreen else Gray
                                )
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    SegmentedButton(
                        selected = selectedView == index,
                        onClick = { selectedView = index },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = tabs.size
                        ),
                        icon = {},
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = ParkHubGreenContainer,
                            activeContentColor = ParkHubGreen,
                            activeBorderColor = ParkHubGreen,
                            inactiveContainerColor = MaterialTheme.colorScheme.surface,
                            inactiveContentColor = Gray,
                            inactiveBorderColor = Gray
                        )
                    ) {
                        Text(
                            text = title,
                            fontWeight = if (selectedView == index)
                                FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${sortierteListe.size} Stellplätze gefunden",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box {
                    TextButton(onClick = { showSortieren = true }) {
                        Text(
                            text = "Sortieren ▾",
                            color = ParkHubGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    DropdownMenu(
                        expanded = showSortieren,
                        onDismissRequest = { showSortieren = false }
                    ) {
                        sortierOptionen.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.label,
                                        color = if (selectedSortierung == option)
                                            ParkHubGreen
                                        else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (selectedSortierung == option)
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedSortierung = option
                                    showSortieren = false
                                },
                                leadingIcon = {
                                    Text(
                                        text = if (option.aufsteigend) "↑" else "↓",
                                        color = if (selectedSortierung == option)
                                            ParkHubGreen else Gray
                                    )
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Karte nur im Karten-Tab
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

        // Sortierte Stellplatz Liste
        items(
            items = sortierteListe,
            key = { it.id }
        ) { stellplatz ->
            StellplatzListeItem(
                stellplatz = stellplatz,
                onClick = { }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}