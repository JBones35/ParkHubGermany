package com.parkhub.app.ui.components.flotte

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.model.Fahrer
import com.parkhub.app.model.FahrerStatus
import com.parkhub.app.ui.theme.Gray
import com.parkhub.app.ui.theme.StatusBesetzt

// Zeigt einen Fahrer mit Icon, Name, Lizenznummer und Status-Badge an.
// Der Status wird von außen übergeben, da er zur Laufzeit aus Buchungen
// und Ausfällen berechnet wird und keine feste Eigenschaft des Fahrers ist.
@Composable
fun FahrerItem(
    fahrer: Fahrer,
    status: FahrerStatus,
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
                            FahrerStatus.EINGESETZT -> Color(0xFFFFE0B2)
                            FahrerStatus.ABWESEND -> Color(0xFFEEEEEE)
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (status == FahrerStatus.EINGESETZT)
                        Icons.Filled.PersonOff else Icons.Outlined.Person,
                    contentDescription = null,
                    tint = when (status) {
                        FahrerStatus.EINGESETZT -> StatusBesetzt
                        FahrerStatus.ABWESEND -> Gray
                        else -> MaterialTheme.colorScheme.onBackground
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fahrer.vorname + " " + fahrer.nachname,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Lizenz: ${fahrer.lizenzNummer}",
                    fontSize = 13.sp,
                    color = Gray
                )
            }

            FahrerStatusBadge(status = status)
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}

// Kompaktes Badge mit Hintergrundfarbe, Textfarbe und Label je nach Status.
@Composable
fun FahrerStatusBadge(status: FahrerStatus) {
    val (backgroundColor, textColor, label) = when (status) {
        FahrerStatus.FREI -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            "Frei"
        )
        FahrerStatus.EINGESETZT -> Triple(
            Color(0xFFFFE0B2),
            Color(0xFFE65100),
            "Eingesetzt"
        )
        FahrerStatus.ABWESEND -> Triple(
            Color(0xFFF5F5F5),
            Color(0xFF616161),
            "Abwesend"
        )
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp)),
        color = backgroundColor
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}