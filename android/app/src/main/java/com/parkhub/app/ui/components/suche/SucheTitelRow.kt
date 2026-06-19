package com.parkhub.app.ui.components.suche

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.theme.ParkHubGreen

// Titelzeile der Suche mit Überschrift und Filter-Icon. Solange ein
// Filter aktiv ist (vom Standard abweicht), wird das Icon grün
// eingefärbt und zusätzlich ein kleiner Punkt oben rechts angezeigt.
@Composable
fun SucheTitelRow(
    onFilterClick: () -> Unit,
    filterAktiv: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Stellplatz suchen",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Box {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filter",
                    tint = if (filterAktiv) ParkHubGreen else MaterialTheme.colorScheme.onBackground
                )
            }
            if (filterAktiv) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(ParkHubGreen)
                )
            }
        }
    }
}