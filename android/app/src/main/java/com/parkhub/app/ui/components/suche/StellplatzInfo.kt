package com.parkhub.app.ui.components.suche

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.theme.Gray
import com.parkhub.app.ui.screens.StellplatzMitDetails
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import com.parkhub.app.ui.theme.ParkHubGreen

@SuppressLint("DefaultLocale")
@Composable
fun StellplatzInfo(stellplatz: StellplatzMitDetails) {
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
                text = stellplatz.adresse?.strasse + " " + stellplatz.adresse?.hausnummer,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stellplatz.entfernungMeter.alsEntfernungText(),
                fontSize = 12.sp,
                color = Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            StellplatzBewertung(
                bewertung = stellplatz.bewertungSchnitt,
                anzahlBewertungen = stellplatz.anzahlBewertungen
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${String.format("%.2f", stellplatz.stellplatz.preis_stunde)} €/h",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = ParkHubGreen
        )
    }
}

@SuppressLint("DefaultLocale")
fun Int.alsEntfernungText(): String = if (this >= 1000)
    "${String.format("%.1f", this / 1000.0)} km"
else "$this m"