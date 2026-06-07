package com.parkhub.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.components.suche.OsmMapView
import com.parkhub.app.ui.theme.*
import org.osmdroid.util.GeoPoint

@Composable
fun SucheScreen() {
    var ort by remember { mutableStateOf("Karlsruhe Innenstadt") }
    var datum by remember { mutableStateOf("8. Mai") }
    var uhrzeit by remember { mutableStateOf("09:00 – 11:00") }
    var fahrzeugTyp by remember { mutableStateOf("Mercedes Sprinter") }
    var selectedView by remember { mutableStateOf(0) }

    val tabs = listOf("Karte", "Liste")

    val markers = listOf(
        Pair(GeoPoint(49.0069, 8.4037), "3,40 €"),
        Pair(GeoPoint(49.0089, 8.4010), "4,20 €"),
        Pair(GeoPoint(49.0050, 8.4060), "2,80 €")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        // Titel + Filter Icon
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

        // Ort Eingabe
        OutlinedTextField(
            value = ort,
            onValueChange = { ort = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Ort") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    tint = ParkHubGreen
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ParkHubGreen,
                focusedLabelColor = ParkHubGreen
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Datum + Uhrzeit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = datum,
                onValueChange = { datum = it },
                modifier = Modifier.weight(1f),
                label = { Text("Datum") },
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
                    focusedLabelColor = ParkHubGreen
                )
            )
            OutlinedTextField(
                value = uhrzeit,
                onValueChange = { uhrzeit = it },
                modifier = Modifier.weight(1f),
                label = { Text("Uhrzeit") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ParkHubGreen,
                    focusedLabelColor = ParkHubGreen
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fahrzeugtyp
        OutlinedTextField(
            value = fahrzeugTyp,
            onValueChange = { fahrzeugTyp = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Fahrzeugtyp") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null,
                    tint = ParkHubGreen
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ParkHubGreen,
                focusedLabelColor = ParkHubGreen
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Karte / Liste Toggle
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

        // Karte
        OsmMapView(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(12.dp)),
            latitude = 49.0069,
            longitude = 8.4037,
            markers = markers
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Ergebnis Zeile
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "12 Stellplätze gefunden",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { }) {
                Text(
                    text = "Sortieren ▾",
                    color = ParkHubGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}