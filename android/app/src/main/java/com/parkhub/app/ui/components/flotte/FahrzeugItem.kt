package com.parkhub.app.ui.components.flotte

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.model.Fahrzeug
import com.parkhub.app.model.FahrzeugStatus
import com.parkhub.app.model.FahrzeugTyp
import com.parkhub.app.ui.theme.Gray
import com.parkhub.app.ui.theme.StatusWartung
import com.parkhub.app.ui.theme.StatusBesetzt

// Zeigt ein Fahrzeug mit Icon, Kennzeichen, Typ und Status-Badge an.
// Der Status wird von außen übergeben, da er zur Laufzeit aus Buchungen
// und Ausfällen berechnet wird und keine feste Eigenschaft des Fahrzeugs ist.
@Composable
fun FahrzeugItem(
    fahrzeug: Fahrzeug,
    fahrzeugTyp: FahrzeugTyp?,
    status: FahrzeugStatus,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon-Hintergrund passt sich farblich dem aktuellen Status an
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (status) {
                            FahrzeugStatus.WARTUNG -> Color(0xFFE0E0E0)
                            FahrzeugStatus.BESETZT -> Color(0xFFFFE0B2)
                            FahrzeugStatus.FREI -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (status) {
                        FahrzeugStatus.WARTUNG -> Icons.Filled.Build
                        FahrzeugStatus.BESETZT -> Icons.Outlined.Schedule
                        FahrzeugStatus.FREI -> Icons.Outlined.LocalShipping
                    },
                    contentDescription = null,
                    tint = when (status) {
                        FahrzeugStatus.WARTUNG -> StatusWartung
                        FahrzeugStatus.BESETZT -> StatusBesetzt
                        FahrzeugStatus.FREI -> MaterialTheme.colorScheme.onBackground
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fahrzeug.kennzeichen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (fahrzeugTyp != null)
                        "${fahrzeugTyp.bezeichnung} · ${fahrzeugTyp.gewicht}"
                    else
                        "Unbekannter Typ",
                    fontSize = 13.sp,
                    color = Gray
                )
            }

            StatusBadge(status = status)
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}