package com.parkhub.app.ui.components.flotte

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocalShipping
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
import com.parkhub.app.ui.theme.Gray
import com.parkhub.app.ui.theme.ParkHubGreen
import com.parkhub.app.ui.theme.ParkHubGreenContainer
import com.parkhub.app.ui.theme.StatusWartung

@Composable
fun FahrzeugItem(fahrzeug: Fahrzeug, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fahrzeug Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (fahrzeug.status == FahrzeugStatus.WARTUNG)
                            Color(0xFFFFE0B2) else ParkHubGreenContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (fahrzeug.status == FahrzeugStatus.WARTUNG)
                        Icons.Filled.Build else Icons.Filled.LocalShipping,
                    contentDescription = null,
                    tint = if (fahrzeug.status == FahrzeugStatus.WARTUNG)
                        StatusWartung else ParkHubGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Kennzeichen + Modell
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fahrzeug.kennzeichen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${fahrzeug.modell} · ${fahrzeug.gewicht}",
                    fontSize = 13.sp,
                    color = Gray
                )
            }

            StatusBadge(status = fahrzeug.status)
        }
    }
}