package com.parkhub.app.ui.components.suche

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.parkhub.app.ui.theme.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Antwortformat der Nominatim-API, sowohl für Vorwärts- als auch
// Rückwärts-Geocoding verwendet.
data class NominatimResult(
    val display_name: String,
    val lat: String,
    val lon: String
)

val nominatimClient = HttpClient(Android) {
    install(ContentNegotiation) {
        gson()
    }
}

// Sucht Orte anhand eines Suchbegriffs (Vorwärts-Geocoding).
suspend fun sucheOrte(query: String): List<NominatimResult> {
    return try {
        nominatimClient.get("https://nominatim.openstreetmap.org/search") {
            parameter("q", query)
            parameter("format", "json")
            parameter("limit", 5)
            parameter("countrycodes", "de")
            header("User-Agent", "ParkHub/1.0")
        }.body()
    } catch (e: Exception) {
        emptyList()
    }
}

// Wandelt GPS-Koordinaten in einen lesbaren Ortsnamen um (Rückwärts-Geocoding).
suspend fun reverseGeocode(lat: Double, lon: Double): String? {
    return try {
        val result: NominatimResult = nominatimClient.get("https://nominatim.openstreetmap.org/reverse") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("format", "json")
            header("User-Agent", "ParkHub/1.0")
        }.body()
        result.display_name
    } catch (e: Exception) {
        null
    }
}

// Texteingabefeld für die Ortssuche mit drei Funktionen: automatische
// Standorterkennung beim Öffnen, Live-Vorschläge während der Eingabe
// (mit Debounce), und Auswahl aus der Vorschlagsliste.
@SuppressLint("MissingPermission")
@Composable
fun OrtSucheField(
    ort: String,
    onOrtChange: (String) -> Unit,
    onOrtSelected: (String, Double, Double) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var vorschlaege by remember { mutableStateOf<List<NominatimResult>>(emptyList()) }
    var zeigeVorschlaege by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isStandortLoading by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                isStandortLoading = true
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                try {
                    // lastLocation zuerst, da im Emulator quasi sofort verfügbar
                    var location = fusedClient.lastLocation.await()

                    if (location == null) {
                        // Fallback nur falls noch nie eine Position bekannt war
                        location = fusedClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            null
                        ).await()
                    }

                    if (location != null) {
                        val name = reverseGeocode(location.latitude, location.longitude)
                        if (name != null) {
                            onOrtChange(name)
                            onOrtSelected(name, location.latitude, location.longitude)
                        }
                    }
                } catch (e: Exception) {
                    // Standort konnte nicht ermittelt werden, Feld bleibt leer
                } finally {
                    isStandortLoading = false
                }
            }
        }
    }

    // Berechtigung wird einmalig beim ersten Anzeigen des Felds angefragt
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Column {
        OutlinedTextField(
            value = ort,
            onValueChange = { input ->
                onOrtChange(input)
                searchJob?.cancel()
                if (input.length >= 3) {
                    searchJob = scope.launch {
                        isLoading = true
                        delay(400) // Debounce, wartet bevor die Suche ausgelöst wird
                        val ergebnisse = sucheOrte(input)
                        vorschlaege = ergebnisse
                        zeigeVorschlaege = ergebnisse.isNotEmpty()
                        isLoading = false
                    }
                } else {
                    vorschlaege = emptyList()
                    zeigeVorschlaege = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Ort") },
            placeholder = { Text("z. B. Karlsruhe Innenstadt") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    tint = ParkHubGreen
                )
            },
            trailingIcon = {
                if (isLoading || isStandortLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = ParkHubGreen,
                        strokeWidth = 2.dp
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ParkHubGreen,
                focusedLabelColor = ParkHubGreen
            ),
            singleLine = true
        )

        // Vorschlagsliste unterhalb des Textfelds
        if (zeigeVorschlaege) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(vorschlaege) { ergebnis ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOrtChange(ergebnis.display_name)
                                    onOrtSelected(
                                        ergebnis.display_name,
                                        ergebnis.lat.toDouble(),
                                        ergebnis.lon.toDouble()
                                    )
                                    zeigeVorschlaege = false
                                    vorschlaege = emptyList()
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Place,
                                contentDescription = null,
                                tint = Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = ergebnis.display_name,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2
                            )
                        }
                        if (vorschlaege.last() != ergebnis) {
                            HorizontalDivider(color = GrayBorder)
                        }
                    }
                }
            }
        }
    }
}