package com.parkhub.app.ui.components.suche

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.theme.Gray
import com.parkhub.app.ui.theme.GrayBorder
import com.parkhub.app.ui.theme.ParkHubGreen

// Zeigt eine Bewertung als 5-Sterne-Reihe plus Zahlenwert und
// Anzahl der Bewertungen an. Volle Sterne entsprechen dem
// abgerundeten Bewertungswert (z.B. 4,7 zeigt 4 volle Sterne).
@Composable
fun StellplatzBewertung(
    bewertung: Float,
    anzahlBewertungen: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < bewertung.toInt())
                    Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (index < bewertung.toInt())
                    ParkHubGreen else GrayBorder,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$bewertung ($anzahlBewertungen)",
            fontSize = 11.sp,
            color = Gray
        )
    }
}