package com.parkhub.app.ui.components.suche

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.screens.StellplatzFilter
import com.parkhub.app.ui.theme.Gray
import com.parkhub.app.ui.theme.ParkHubGreen
import com.parkhub.app.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StellplatzFilterDialog(
    show: Boolean,
    filter: StellplatzFilter,
    ergebnisAnzahl: Int,
    onFilterChange: (StellplatzFilter) -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Filter", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Zurücksetzen",
                    fontSize = 14.sp,
                    color = ParkHubGreen,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            val standard = StellplatzFilter()
                            onFilterChange(
                                filter.copy(
                                    minLaenge = standard.minLaenge,
                                    minBreite = standard.minBreite,
                                    minHoehe = standard.minHoehe,
                                    minPreis = standard.minPreis,
                                    maxPreis = standard.maxPreis,
                                    minBewertung = standard.minBewertung
                                )
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ===== MINDESTLÄNGE =====
            Text(text = "Mindestlänge", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "3,0 m", fontSize = 12.sp, color = Gray)
                Text(text = "8,0 m", fontSize = 12.sp, color = Gray)
            }
            Slider(
                value = filter.minLaenge / 100f,
                onValueChange = { onFilterChange(filter.copy(minLaenge = it * 100f)) },
                valueRange = 3f..8f,
                colors = SliderDefaults.colors(
                    thumbColor = ParkHubGreen,
                    activeTrackColor = ParkHubGreen
                )
            )
            Text(
                text = "Min. ${"%.1f".format(filter.minLaenge / 100f)} m",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== MINDESTBREITE =====
            Text(text = "Mindestbreite", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "1,8 m", fontSize = 12.sp, color = Gray)
                Text(text = "3,0 m", fontSize = 12.sp, color = Gray)
            }
            Slider(
                value = filter.minBreite / 100f,
                onValueChange = { onFilterChange(filter.copy(minBreite = it * 100f)) },
                valueRange = 1.8f..3.0f,
                colors = SliderDefaults.colors(
                    thumbColor = ParkHubGreen,
                    activeTrackColor = ParkHubGreen
                )
            )
            Text(
                text = "Min. ${"%.1f".format(filter.minBreite / 100f)} m",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== MINDESTHÖHE =====
            Text(text = "Mindesthöhe", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "1,8 m", fontSize = 12.sp, color = Gray)
                Text(text = "3,2 m", fontSize = 12.sp, color = Gray)
            }
            Slider(
                value = filter.minHoehe / 100f,
                onValueChange = { onFilterChange(filter.copy(minHoehe = it * 100f)) },
                valueRange = 1.8f..3.2f,
                colors = SliderDefaults.colors(
                    thumbColor = ParkHubGreen,
                    activeTrackColor = ParkHubGreen
                )
            )
            Text(
                text = "Min. ${"%.1f".format(filter.minHoehe / 100f)} m",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

// ===== PREIS PRO STUNDE =====
            Text(text = "Preis pro Stunde (€)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            RangeSlider(
                value = filter.minPreis..filter.maxPreis,
                onValueChange = { range ->
                    onFilterChange(filter.copy(minPreis = range.start, maxPreis = range.endInclusive))
                },
                valueRange = 2f..8f,
                colors = SliderDefaults.colors(
                    thumbColor = ParkHubGreen,
                    activeTrackColor = ParkHubGreen
                )
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "2,00 €", fontSize = 12.sp, color = Gray)
                Text(text = "8,00 €", fontSize = 12.sp, color = Gray)
            }
            Text(
                text = "${"%.2f".format(filter.minPreis)} € – ${"%.2f".format(filter.maxPreis)} €",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== MINDESTBEWERTUNG =====
            Text(text = "Mindestbewertung", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filter.minBewertung == 0f,
                    onClick = { onFilterChange(filter.copy(minBewertung = 0f)) },
                    label = { Text("Alle", fontSize = 13.sp) }
                )
                FilterChip(
                    selected = filter.minBewertung == 4f,
                    onClick = { onFilterChange(filter.copy(minBewertung = 4f)) },
                    label = { Text("★ 4+", fontSize = 13.sp) }
                )
                FilterChip(
                    selected = filter.minBewertung == 4.5f,
                    onClick = { onFilterChange(filter.copy(minBewertung = 4.5f)) },
                    label = { Text("★ 4,5+", fontSize = 13.sp) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ParkHubGreen),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(
                    text = "$ergebnisAnzahl Ergebnisse anzeigen",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White
                )
            }
        }
    }
}