package com.parkhub.app.ui.components.suche

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.theme.Gray
import com.parkhub.app.ui.screens.StellplatzVorschau
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import com.parkhub.app.ui.theme.ParkHubGreen

@Composable
fun StellplatzInfo(stellplatz: StellplatzVorschau) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Place,
            contentDescription = null,
            tint = ParkHubGreen,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stellplatz.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stellplatz.entfernungText,
                fontSize = 12.sp,
                color = Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            StellplatzBewertung(
                bewertung = stellplatz.bewertung,
                anzahlBewertungen = stellplatz.anzahlBewertungen
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${String.format("%.2f", stellplatz.preisProStunde)} €/h",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = ParkHubGreen
        )
    }
}